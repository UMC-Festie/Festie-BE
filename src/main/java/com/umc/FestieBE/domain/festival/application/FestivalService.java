package com.umc.FestieBE.domain.festival.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.image.AwsS3Service;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;
import static com.umc.FestieBE.global.type.FestivalType.FESTIVAL;
import static com.umc.FestieBE.global.type.FestivalType.PERFORMANCE;

@Service
@RequiredArgsConstructor
public class FestivalService {
    private final FestivalRepository festivalRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LikeOrDislikeRepository likeOrDislikeRepository;

    /** (Redis) 최근 조회 내역 */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveRecentFestivals(Long userId, List<Map<String, String>> festivals) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String cacheKey = "recentFestivals:" + userId; // Cache Key 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String festivalsJson;

        // 최대 누적 개수 설정 (8개로 설정)
        int maxRecentFestivals = 8;

        // 누적 개수가 최대 개수를 초과하면 가장 오래된 정보부터 제거
        if (festivals.size() > maxRecentFestivals) {
            festivals = festivals.subList(festivals.size() - maxRecentFestivals, festivals.size());
        }

        try {
            festivalsJson = objectMapper.writeValueAsString(festivals);
            Duration expiration = Duration.ofDays(7); // Key를 저장할 때 만료 시간을 설정 (예: 7일 후에 만료)
            vop.set(cacheKey, festivalsJson, expiration); // cacheKey를 사용하여 데이터 저장 및 만료 설정
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // 최근 조회한 축제 정보를 Redis에서 가져오는 메서드
    public List<Map<String, String>> getRecentFestivals(Long userId) {
        String cacheKey = "recentFestivals:" + userId; // Cache Key 생성
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String festivalsJson = vop.get(cacheKey);

        if (festivalsJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Map<String, String>> festivals = objectMapper.readValue(festivalsJson, new TypeReference<List<Map<String, String>>>() {});
                Collections.reverse(festivals); // 역순으로 정렬 (마지막으로 조회한 내역부터 보여줌)
                return festivals;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    // 최근 조회한 축제 정보를 Map으로 변환
    private Map<String, String> festivalToMap(Festival festival) {
        Map<String, String> festivalInfo = new HashMap<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.dd");
        String startDate = festival.getStartDate().format(dateFormatter);
        String endDate = festival.getEndDate().format(dateFormatter);
        String festivalDate = startDate + " - " + endDate;

        festivalInfo.put("festivalId", festival.getId().toString());
        festivalInfo.put("festivalTitle", festival.getFestivalTitle());
        festivalInfo.put("duration", festival.getDuration());
        festivalInfo.put("thumbnailUrl", festival.getThumbnailUrl());
        festivalInfo.put("location", festival.getLocation());
        festivalInfo.put("festivalDate", festivalDate);
        festivalInfo.put("festivalType", festival.getType().getType());
        return festivalInfo;
    }

    // 최근 조회 내역 업데이트
    private void updateRecentFestivals(Long userId, List<Map<String, String>> recentFestivals, Map<String, String> newFestivalInfo) {
        String newFestivalId = newFestivalInfo.get("festivalId");

        // 동일한 축제 ID가 이미 최근 조회한 목록에 있는지 확인하고 있다면 해당 정보 업데이트
        for (Map<String, String> festivalInfo : recentFestivals) {
            if (festivalInfo.get("festivalId").equals(newFestivalId)) {
                // 기존의 정보를 새로운 정보로 업데이트
                festivalInfo.putAll(newFestivalInfo);
                saveRecentFestivals(userId, recentFestivals); // 업데이트된 목록 저장
                return;
            }
        }

        // 최근 조회한 목록에 없으면 새로운 정보 추가
        recentFestivals.add(newFestivalInfo);
        saveRecentFestivals(userId, recentFestivals); // 업데이트된 목록 저장
    }


    /** 새로운 공연, 축제 상세 조회 */
    public FestivalResponseDTO.FestivalDetailResponse getFestival(FestivalService festivalService, Long festivalId, HttpServletRequest request) {
        // 조회수 업데이트
        festivalRepository.updateView(festivalId);

        Festival festival = festivalRepository.findByIdWithUser(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        // 게시글 작성자인지 확인
        boolean isWriter = false;
        Long userId = jwtTokenProvider.getUserIdByServlet(request);
        if (userId != null && userId == festival.getUser().getId()) {
            isWriter = true;
        }

        String dDay = festivalService.calculateDday(festivalId);
        FestivalResponseDTO.FestivalDetailResponse festivalDetail;

        // 유저가 좋아요/싫어요를 눌렀는지 여부 확인
        Integer isLikedOrDisliked = null;
        if (userId != null) {
            List<LikeOrDislike> likeOrDislike = likeOrDislikeRepository.findByFestivalIdAndUserId(festivalId, userId);
            if (!likeOrDislike.isEmpty()) {
                isLikedOrDisliked = likeOrDislike.get(0).getStatus();
            }
        }

        if (userId != null) {
            List<Map<String, String>> recentFestivals = getRecentFestivals(userId);
            Map<String, String> festivalInfo = festivalToMap(festival);
            updateRecentFestivals(userId, recentFestivals, festivalInfo);
            saveRecentFestivals(userId, recentFestivals);

            // 여기에서 최신 정보로 업데이트된 recentFestivals 리스트를 가지고 상세조회 로직 수행
            festivalDetail = new FestivalResponseDTO.FestivalDetailResponse(festival, isWriter, dDay, isLikedOrDisliked);
        } else {
            festivalDetail = new FestivalResponseDTO.FestivalDetailResponse(festival, isWriter, dDay, isLikedOrDisliked);
        }

        return festivalDetail;
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

        // postTitle는 필수값 아님! -> 따라서 postTitle이 null일 경우 FestTitle로 설정
        String postTitle = request.getPostTitle();
        if (postTitle == null){
            postTitle = request.getFestivalTitle();
        }

        Festival festival = request.toEntity(user, festivalType, region, category, isDeleted, imagesUrl, thumbnailUrl, postTitle);
        festivalRepository.save(festival);
    }


    /** 새로운 공연,축제 수정 */
    public void updateFestival(Long festivalId, FestivalRequestDTO request, List<MultipartFile> images, MultipartFile thumbnail) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        // 게시글 수정 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != festival.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "새로운 공연/축제 게시글 수정 권한이 없습니다.");
        }

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

        // 게시글 삭제 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != festival.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "새로운 공연/축제 게시글 삭제 권한이 없습니다.");
        }

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
    public FestivalResponseDTO.FestivalListResponse fetchFestivalPage(int page,
                                                                      String sortBy,
                                                                      String category,
                                                                      String region,
                                                                      String duration) {
        CategoryType categoryType = null;
        if (category != null){
            categoryType = CategoryType.findCategoryType(category);
        }

        RegionType regionType =  null;
        if (region != null) {
            regionType = RegionType.findRegionType(region);
        }

        PageRequest pageRequest = PageRequest.of(page, 16);
        Page<Festival> festivalPage = festivalRepository.findAllFestival(sortBy, categoryType, regionType, duration, pageRequest);
        List<Festival> festivalList = festivalPage.getContent();

        int pageNum = festivalPage.getNumber(); // 현재 페이지 수
        boolean hasNext = festivalPage.hasNext(); // 다음 페이지 존재 여부
        boolean hasPrevious = festivalPage.hasPrevious(); // 이전 페이지 존재 여부

       List<FestivalResponseDTO.FestivalPaginationResponse> data = festivalList.stream()
                .filter(festival -> !festival.getIsDeleted())
                .map(festival -> new FestivalResponseDTO.FestivalPaginationResponse(festival, calculateDday(festival.getId())))
                .collect(Collectors.toList());

       long totalCount = data.size(); // 총 검색 수

       return new FestivalResponseDTO.FestivalListResponse(data, totalCount, pageNum, hasNext, hasPrevious);
    }
}
