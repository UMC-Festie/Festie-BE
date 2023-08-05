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
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.image.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.umc.FestieBE.global.exception.CustomErrorCode.IMAGE_UPLOAD_LIMIT_EXCEEDED;
import static com.umc.FestieBE.global.exception.CustomErrorCode.TICKETING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TicketingService {
    private final TicketingRepository ticketingRepository;
    private final FestivalRepository festivalRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final AwsS3Service awsS3Service;

    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    /** 티켓팅 상세 조회 */
    public TicketingResponseDTO getTicketing(Long ticketingId) {
        // 조회수 업뎃
        ticketingRepository.updateView(ticketingId);

        // 티켓팅 게시글 상세 조회
        Ticketing ticketing = ticketingRepository.findByIdWithUser(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

        // TODO 게시글 작성자 조회 -> isWriter
        Boolean isWriter = null;

        Long like = likeOrDislikeRepository.findByTargetIdTestWithStatus(1, null, ticketingId, null);
        Long dislike = likeOrDislikeRepository.findByTargetIdTestWithStatus(0, null, ticketingId, null);

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
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

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
            ticketing = request.toEntity(tempUser, festival, imagesUrl);
        } else { // 2. 축제, 공연 연동 X
            String thumbnailUrl = null;
            if (thumbnail != null) {
                thumbnailUrl = awsS3Service.uploadImgFile(thumbnail);
            }

            ticketing = request.toEntity(tempUser, thumbnailUrl, imagesUrl);
        }

        ticketingRepository.save(ticketing);
    }

    /** 티켓팅 삭제 */
    public void deleteTicketing(Long ticketingId) {
        Ticketing ticketing = ticketingRepository.findById(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));
        ticketingRepository.delete(ticketing);
    }

    /** 티켓팅 수정 */
    public void updateTicketing(Long ticketingId, TicketingRequestDTO request) {
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Ticketing ticketing = ticketingRepository.findById(ticketingId)
                .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

        if (request.getFestivalId() != null) { // 1. 새롭게 연동할 경우
            Festival festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));

            ticketing.clearFestivalInfo(); // 기존에 연동된 내용 삭제

            ticketing.updateTicketing( // 새롭게 다시 작성한 글 업데이트
                    request.getFestivalId(),
                    festival.getTitle(),
                    festival.getThumbnailUrl(),
                    request.getTicketingDate(),
                    request.getTicketingTime(),
                    request.getTitle(),
                    request.getContent()
            );
        }
        else { // 2. 연동 안할 경우 -> 사용자가 다시 재작성
            ticketing.clearFestivalInfo(); // 기존에 연동된 내용 삭제

            ticketing.updateTicketing( // 새롭게 다시 작성한 글 업데이트
                    request.getFestivalId(),
                    request.getFestivalTitle(),
                    request.getThumbnailUrl(),
                    request.getTicketingDate(),
                    request.getTicketingTime(),
                    request.getTitle(),
                    request.getContent()
            );
        }
        ticketingRepository.save(ticketing);
    }
}
