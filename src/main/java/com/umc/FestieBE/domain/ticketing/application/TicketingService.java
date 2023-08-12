package com.umc.FestieBE.domain.ticketing.application;

import com.sun.xml.bind.v2.TODO;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkTicketingResponseDTO;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class TicketingService {
    private final TicketingRepository ticketingRepository;
    private final FestivalRepository festivalRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /** 티켓팅 상세 조회 */
    public TicketingResponseDTO getTicketing(Long ticketingId) {
        // 조회수 업뎃
        ticketingRepository.updateView(ticketingId);

        // 티켓팅 게시글 상세 조회
        Ticketing ticketing = ticketingRepository.findByIdWithUser(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

        // TODO 게시글 작성자 조회 -> isWriter
        Boolean isWriter = null;

        Long like = likeOrDislikeRepository.findByTargetIdTestWithStatus(1, null, ticketingId, null,null);
        Long dislike = likeOrDislikeRepository.findByTargetIdTestWithStatus(0, null, ticketingId, null,null);

        // 공연, 축제 연동 여부
        boolean isLinked = false;
        FestivalLinkTicketingResponseDTO festivalInfo;

        // 공연, 축제 연동 O
        if (ticketing.getFestivalId() != null){
            isLinked = true;
            Festival linkedFestival = festivalRepository.findById(ticketing.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));

            festivalInfo = new FestivalLinkTicketingResponseDTO(linkedFestival);
        }
        else { // 공연, 축제 연동 X
            festivalInfo = new FestivalLinkTicketingResponseDTO(ticketing);
        }
        return new TicketingResponseDTO(ticketing, isLinked, isWriter, festivalInfo, like, dislike);
    }

    /** 티켓팅 등록 */
    public void createTicketing(TicketingRequestDTO request, List<MultipartFile> images, MultipartFile thumbnail) {
        // 유저
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Festival festival;
        Ticketing ticketing;

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
            festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            ticketing = request.toEntity(user, festival, imagesUrl);
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
            Festival festival = festivalRepository.findById(request.getFestivalId())
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
}
