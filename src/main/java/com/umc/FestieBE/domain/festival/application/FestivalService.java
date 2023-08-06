package com.umc.FestieBE.domain.festival.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalPaginationResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.image.AwsS3Service;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import com.umc.FestieBE.global.type.SortedType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;
import static com.umc.FestieBE.global.type.FestivalType.FESTIVAL;
import static com.umc.FestieBE.global.type.FestivalType.PERFORMANCE;

@Service
@RequiredArgsConstructor
public class FestivalService {
    private final FestivalRepository festivalRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    /** 새로운 공연, 축제 상세 조회 */
    public FestivalResponseDTO getFestival(FestivalService festivalService, Long festivalId){
        // 조회수 업데이트
        festivalRepository.updateView(festivalId);

        Festival festival = festivalRepository.findByIdWithUser(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        // TODO isWriter 확인

        Boolean isWriter = null;
        String dDay = festivalService.calculateDday(festivalId);

        // 좋아요, 싫어요
        Long likes = likeOrDislikeRepository.findByTargetIdTestWithStatus(1, festivalId,null,null);
        Long dislikes = likeOrDislikeRepository.findByTargetIdTestWithStatus(0, festivalId,null,null);

        festival.addLikes(likes);
        festivalRepository.save(festival);

        return new FestivalResponseDTO(festival, isWriter, dDay, likes, dislikes);
    }

    /** 새로운 공연,축제 등록 */
    public void createFestival(FestivalRequestDTO request, List<MultipartFile> images, MultipartFile thumbnail) {
        // 유저
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        RegionType region = RegionType.findRegionType(request.getFestivalRegion());
        CategoryType category = CategoryType.findCategoryType(request.getCategory());

        Boolean isDeleted = false;

        int maxImageUpload = 5; // 이미지 최대 5장 업로드 가능

        if (images.size() > maxImageUpload) {
            throw new CustomException(IMAGE_UPLOAD_LIMIT_EXCEEDED);
        }

        // 이미지 파일들을 업로드하고 URL을 얻어옴
        //List<String> imagesUrl = new ArrayList<>();

        //for (MultipartFile image : images) {
        //    String _imagesUrl = awsS3Service.uploadImgFile(image); // awsS3Service를 사용하여 이미지 업로드
        //    imagesUrl.add(_imagesUrl);

        List<String> imagesUrl = null;
        if (!images.isEmpty()) {
            imagesUrl = new ArrayList<>();
            for (MultipartFile image : images) {
                String _imagesUrl = awsS3Service.uploadImgFile(image);
                imagesUrl.add(_imagesUrl);
            }
        }

        String thumbnailUrl = null;
        if (!thumbnail.isEmpty()) {
            thumbnailUrl = awsS3Service.uploadImgFile(thumbnail); // 썸네일 이미지
        }

        Festival festival = request.toEntity(user, festivalType, region, category, isDeleted, imagesUrl, thumbnailUrl);
        festivalRepository.save(festival);
    }

    /** 새로운 공연,축제 수정 */
    public void updateFestival(Long festivalId, FestivalRequestDTO request, List<MultipartFile> images, MultipartFile thumbnail) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        RegionType region = RegionType.findRegionType(request.getFestivalRegion());
        CategoryType category = CategoryType.findCategoryType(request.getCategory());
        Boolean isDeleted = false;


        // 기존에 있던 이미지, 썸네일 삭제 후 재 업로드
        if (request.getImagesUrl() != null) {
            List<String> getImagesUrl = request.getImagesUrl(); // 기존에 등록된 이미지 url

            for (String _getImagesUrl : getImagesUrl) {
                awsS3Service.deleteImage(_getImagesUrl); // AWS s3에 등록된 이미지 삭제
            }
        }

        if (request.getThumbnailUrl() != null ) {
            String getThumbnailUrl = request.getThumbnailUrl(); // 기존에 등록된 썸네일 url
            awsS3Service.deleteImage(getThumbnailUrl); // AWS s3에 등록된 썸네일 삭제
        }


        // 수정한 이미지 업로드
        int maxImageUpload = 5; // 이미지 최대 5장 업로드 가능

        if (images.size() > maxImageUpload) {
            throw new CustomException(IMAGE_UPLOAD_LIMIT_EXCEEDED);
        }


        //List<String> imagesUrl = new ArrayList<>();

        //for (MultipartFile image : images) {
        //    String _imagesUrl = awsS3Service.uploadImgFile(image);
        //    imagesUrl.add(_imagesUrl);
        //}

        // 수정한 썸네일 업로드
        //String thumbnailUrl = awsS3Service.uploadImgFile(thumbnail);

        // 이미지 파일들을 업로드하고 URL을 얻어옴
        List<String> imagesUrl = null;
        if (!images.isEmpty()) {
            imagesUrl = new ArrayList<>();
            for (MultipartFile image : images) {
                String _imagesUrl = awsS3Service.uploadImgFile(image);
                imagesUrl.add(_imagesUrl);
            }
        }

        // 수정한 썸네일 업로드
        String thumbnailUrl = null;
        if (!thumbnail.isEmpty()) {
            thumbnailUrl = awsS3Service.uploadImgFile(thumbnail);
        }

        festival.updateFestival(
                request.getFestivalTitle(),
                festivalType,
                category,
                region,
                request.getFestivalLocation(),
                request.getFestivalStartDate(),
                request.getFestivalEndDate(),
                request.getFestivalStartTime(),
                request.getReservationLink(),
                request.getPostTitle(),
                request.getContent(),
                request.getFestivalAdminsName(),
                request.getFestivalAdminsPhone(),
                request.getFestivalAdminsSiteAddress(),
                isDeleted,
                imagesUrl,
                thumbnailUrl
        );
        festivalRepository.save(festival);
    }

    /**
     * [새로운 공연, 축제 삭제]
     * 새로운 공연, 축제 삭제 시 해당 데이터가 진짜 삭제 되면 안됨
     * : 데이터 삭제 안하고 isDeleted값이 true가 되도록 함
     */
    public void deleteFestival(Long festivalId, Boolean isDeleted) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        festival.deleteFestival(isDeleted);
        festivalRepository.save(festival);
    }

    /** 디데이 설정 메서드 */
    public String calculateDday(Long festivalId){
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        LocalDate startDate = festival.getStartDate();
        LocalDate endDate = festival.getEndDate();
        LocalDate currentDate = LocalDate.now(); // 유저 로컬 날짜

        Long dDayCount = ChronoUnit.DAYS.between(currentDate, startDate);

        String dDay = "";
        String type = festival.getType().getType(); // 축제 or 공연

        if (PERFORMANCE == festival.getType() || FESTIVAL == festival.getType()) {
            if (currentDate.isBefore(startDate)) {
                dDay = "D-" + dDayCount;
            } else if (currentDate.isAfter(endDate)) {
                dDay = type + "종료";
            } else {
                dDay = type + "중";
            }
        }

        return dDay;
    }

    /** 무한 스크롤 */
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 8); // 걍 상수로 뺐음

    public List<FestivalPaginationResponseDTO> fetchFestivalPage(String sortBy,
                                                                 CategoryType category,
                                                                 RegionType region,
                                                                 String duration) {
        SortedType sortedType = SortedType.findBySortBy(sortBy); // sortBy 값을 SortedType로 변환

        Page<Festival> festivalPage = festivalRepository.findAllTogether(sortedType.name(), category, region, duration, PAGE_REQUEST);
        List<Festival> festivalList = festivalPage.getContent();
        Long totalCount = festivalPage.getTotalElements();
        //Integer totalCount = festivalPage.getSize();

        return festivalList.stream()
                .filter(festival -> !festival.getIsDeleted())
                .map(festival -> new FestivalPaginationResponseDTO(festival, calculateDday(festival.getId()), totalCount))
                .collect(Collectors.toList());
    }
}
