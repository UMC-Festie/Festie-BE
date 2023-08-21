package com.umc.FestieBE.domain.ticketing.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkTicketingResponseDTO;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.ticketing.dto.TicketingRequestDTO;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.image.AwsS3Service;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketingService {
    private final TicketingRepository ticketingRepository;
    private final FestivalRepository festivalRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final OpenFestivalRepository openFestivalRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LikeOrDislikeRepository likeOrDislikeRepository;


    /** 티켓팅 상세 조회 */
    public TicketingResponseDTO.TicketingDetailResponse getTicketing(Long ticketingId, HttpServletRequest request) {
        // 조회수 업뎃
        ticketingRepository.updateView(ticketingId);

        // 티켓팅 게시글 상세 조회
        Ticketing ticketing = ticketingRepository.findByIdWithUser(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

        boolean isWriter = false;
        Long userId = jwtTokenProvider.getUserIdByServlet(request);
        if(userId != null && userId == ticketing.getUser().getId()) {
            isWriter = true;
        }

        // 공연, 축제 연동 여부
        Boolean isLinked = false;
        FestivalLinkTicketingResponseDTO.FestivalLinkTicketingResponse festivalInfo = null;
        FestivalLinkTicketingResponseDTO.OpenFestivalLinkTicketingResponse openFestivalInfo = null;

        // TODO 연동 여부에 따라 썸네일 값 변경
        String thumbnail = null;

        // 유저가 좋아요/싫어요를 눌렀는지 여부 확인
        Integer isLikedOrDisliked = null;

        if (userId != null) {
            List<LikeOrDislike> likeOrDislike = likeOrDislikeRepository.findByTicketingIdAndUserId(ticketingId, userId);
            if (!likeOrDislike.isEmpty()) {
                isLikedOrDisliked = likeOrDislike.get(0).getStatus();
            }
        }

        // 공연, 축제 연동 O
        Boolean isDeleted = false;
        String festivalId = ticketing.getFestivalId();
        if (festivalId != null){
            isLinked = true;

            if(ticketing.getBoardType().equals("정보공유")){
                Festival linkedInfo = festivalRepository.findById(Long.valueOf(festivalId))
                        .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
                festivalInfo = new FestivalLinkTicketingResponseDTO.FestivalLinkTicketingResponse(linkedInfo);
                thumbnail = linkedInfo.getThumbnailUrl();
            }else if(ticketing.getBoardType().equals("정보보기")) {
                if (ticketing.getType().getType().equals("공연")) {
                    OpenPerformance linkedOpenPerformance = openPerformanceRepository.findById(festivalId)
                            .orElseGet(() -> {
                                return null;
                            });
                    isDeleted = linkedOpenPerformance == null;
                    openFestivalInfo = new FestivalLinkTicketingResponseDTO.OpenFestivalLinkTicketingResponse(linkedOpenPerformance, isDeleted);
                } else if (ticketing.getType().getType().equals("축제")) {
                    OpenFestival linkedOpenFestival = openFestivalRepository.findById(festivalId)
                            .orElseGet(() -> {
                                return null;
                            });
                    isDeleted = linkedOpenFestival == null;
                    openFestivalInfo = new FestivalLinkTicketingResponseDTO.OpenFestivalLinkTicketingResponse(linkedOpenFestival, isDeleted);
                }
            }else{
                throw new CustomException(INVALID_VALUE, "공연/축제 게시글 유형은 '정보보기' 또는 '정보공유'만 가능합니다.");
            }
        }
        else { // 공연, 축제 연동 X
            festivalInfo = new FestivalLinkTicketingResponseDTO.FestivalLinkTicketingResponse(ticketing);
            thumbnail = ticketing.getThumbnailUrl();
        }

        return new TicketingResponseDTO.TicketingDetailResponse(ticketing, isLinked, isWriter, festivalInfo, isLikedOrDisliked, thumbnail);
    }


    /** 티켓팅 등록 */
    public void createTicketing(TicketingRequestDTO request, List<MultipartFile> images, MultipartFile thumbnail) {
        // 유저
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Festival festival;
        Ticketing ticketing = null;

        List<String> imagesUrl = new ArrayList<>();
        if (images != null) {
            int maxImageUpload = 5;

            if(images.size() > maxImageUpload) {
                throw new CustomException(IMAGE_UPLOAD_LIMIT_EXCEEDED);
            }

            for (MultipartFile image : images) {
                String _imagesUrl = awsS3Service.uploadImgFile(image);
                imagesUrl.add(_imagesUrl);
            }
        }


        // 공연/축제 정보 연동 시 DB 에서 확인
        if(request.getFestivalId() != null) { // 1. 축제, 공연 연동 O
            if(request.getBoardType().equals("정보보기")){
                // 축제
                Optional<OpenFestival> openFestival = openFestivalRepository.findById(request.getFestivalId());
                if(openFestival.isPresent()){
                    OpenFestival of = openFestival.get();
                    ticketing = request.toEntity(user, FestivalType.findFestivalType("축제"), of.getFestivalTitle(), of.getDetailUrl(), imagesUrl);
                }

                // 공연
                Optional<OpenPerformance> openPerformance = openPerformanceRepository.findById(request.getFestivalId());
                if(openPerformance.isPresent()){
                    OpenPerformance op = openPerformance.get();
                    ticketing = request.toEntity(user, FestivalType.findFestivalType("공연"), op.getFestivalTitle(), op.getDetailUrl(), imagesUrl);
                }
            } else if(request.getBoardType().equals("정보공유")){
                festival = festivalRepository.findById(Long.valueOf(request.getFestivalId()))
                        .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));

                // String title = festival.getFestivalTitle();
                ticketing = request.toEntity(user, festival.getType(), festival.getFestivalTitle(), festival.getThumbnailUrl(), imagesUrl);
            }
        } else { // 2. 축제, 공연 연동 X
            String thumbnailUrl = null;
            if (thumbnail != null) {
                thumbnailUrl = awsS3Service.uploadImgFile(thumbnail);
            }

            ticketing = request.toEntity(user, thumbnailUrl, imagesUrl);
        }

        ticketingRepository.save(ticketing);
    }

    /** 티켓팅 삭제 */
    public void deleteTicketing(Long ticketingId) {

        Ticketing ticketing = ticketingRepository.findById(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

        // 게시글 삭제 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != ticketing.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "티켓팅 게시글 삭제 권한이 없습니다.");
        }

        // 티켓팅에 연관된 likeOrDislike 삭제
        List<LikeOrDislike> likesOrDislikes = likeOrDislikeRepository.findByTicketingId(ticketingId);
        for (LikeOrDislike likeOrDislike : likesOrDislikes) {
            likeOrDislikeRepository.delete(likeOrDislike);
        }

        ticketingRepository.delete(ticketing);
    }

    /** 티켓팅 수정 */
    public void updateTicketing(Long ticketingId, TicketingRequestDTO request, List<MultipartFile> images, MultipartFile thumbnail) {

        Ticketing ticketing = ticketingRepository.findById(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

        // 게시글 수정 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != ticketing.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "티켓팅 게시글 수정 권한이 없습니다.");
        }

        if (request.getFestivalId() != null) { // 1. 새롭게 연동할 경우
            Festival festival = festivalRepository.findById(Long.valueOf(request.getFestivalId()))
                    .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));

            ticketing.clearFestivalInfo(); // 기존에 연동된 내용 삭제

            // 기존에 있던 이미지, 썸네일 삭제 후 재 업로드
            if (request.getImagesUrl() != null) {
                List<String> getImagesUrl = request.getImagesUrl(); // 기존에 등록된 이미지 url

                for (String _getImagesUrl : getImagesUrl) {
                    awsS3Service.deleteImage(_getImagesUrl); // AWS s3에 등록된 이미지 삭제
                }
            }

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

            ticketing.updateTicketing( // 새롭게 다시 작성한 글 업데이트
                    request.getFestivalId(),
                    festival.getTitle(),
                    festival.getThumbnailUrl(),
                    request.getTicketingDate(),
                    request.getTicketingTime(),
                    request.getTitle(),
                    request.getContent(),
                    imagesUrl
            );
        }
        else { // 2. 연동 안할 경우 -> 사용자가 다시 재작성
            ticketing.clearFestivalInfo(); // 기존에 연동된 내용 삭제

            // 기존에 있던 이미지, 썸네일 삭제 후 재 업로드
            if (request.getImagesUrl() != null) {
                List<String> getImagesUrl = request.getImagesUrl(); // 기존에 등록된 이미지 url

                for (String _getImagesUrl : getImagesUrl) {
                    awsS3Service.deleteImage(_getImagesUrl); // AWS s3에 등록된 이미지 삭제
                }
            }

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

            if (request.getThumbnailUrl() != null ) {
                String getThumbnailUrl = request.getThumbnailUrl(); // 기존에 등록된 썸네일 url
                awsS3Service.deleteImage(getThumbnailUrl); // AWS s3에 등록된 썸네일 삭제
            }

            // 수정한 썸네일 업로드
            String thumbnailUrl = null;
            if (thumbnail != null) {
                thumbnailUrl = awsS3Service.uploadImgFile(thumbnail);
            }

            ticketing.updateTicketing( // 새롭게 다시 작성한 글 업데이트
                    request.getFestivalId(),
                    request.getFestivalTitle(),
                    thumbnailUrl,
                    request.getTicketingDate(),
                    request.getTicketingTime(),
                    request.getTitle(),
                    request.getContent(),
                    imagesUrl
            );
        }
        ticketingRepository.save(ticketing);
    }

    /** Pagination (무한 스크롤 X) */
    public TicketingResponseDTO.TicketingListResponse fetchTicketingPage(int page,
                                                                         String sortBy) {

        PageRequest pageRequest = PageRequest.of(page, 6);
        Page<Ticketing> ticketingPage = ticketingRepository.findAllTicketing(sortBy, pageRequest);
        List<Ticketing> ticketingList = ticketingPage.getContent();

        int pageNum = ticketingPage.getNumber(); // 현재 페이지 수
        boolean hasNext = ticketingPage.hasNext(); // 다음 페이지 존재 여부
        boolean hasPrevious = ticketingPage.hasPrevious(); // 이전 페이지 존재 여부

        List<TicketingResponseDTO.TicketingPaginationResponse> data = ticketingList.stream()
                .map(TicketingResponseDTO.TicketingPaginationResponse::new)
                .collect(Collectors.toList());

        long totalCount = data.size(); // 총 검색 수

        return new TicketingResponseDTO.TicketingListResponse(data, totalCount, pageNum, hasNext, hasPrevious);
    }
}
