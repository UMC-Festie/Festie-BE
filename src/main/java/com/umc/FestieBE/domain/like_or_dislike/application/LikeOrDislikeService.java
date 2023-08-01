package com.umc.FestieBE.domain.like_or_dislike.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.like_or_dislike.dto.LikeOrDislikeRequestDTO;
import com.umc.FestieBE.domain.review.dao.ReviewRepository;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserRepository;
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LikeOrDislikeService {

    private final FestivalRepository festivalRepository;
    private final TicketingRepository ticketingRepository;
    private final ReviewRepository reviewRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final TemporaryUserRepository temporaryUserRepository;

    /**
     * 게시글 좋아요/싫어요
     */
    public void createLikeOrDislike(LikeOrDislikeRequestDTO request){

        // 임시 유저(userId: 1) 생성 가정
        TemporaryUser tempUser = temporaryUserRepository.findById(1L).get();

        // 로그인한 유저인지 확인

        Festival festival = null;
        Ticketing ticketing = null;
        Review review = null;

        Long festivalId = request.getFestivalId();
        Long ticketingId = request.getTicketingId();
        Long reviewId = request.getReviewId();

        if(festivalId == null && ticketingId == null && reviewId == null){
            throw new CustomException(CustomErrorCode.LIKES_TARGET_NOT_FOUND);
        }

        // 게시글 조회
        if(festivalId != null){
            festival = festivalRepository.findById(festivalId)
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
        }else if(ticketingId != null){
            ticketing = ticketingRepository.findById(ticketingId)
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.TICKETING_NOT_FOUND)));
        }else if(reviewId != null){
            review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.REVIEW_NOT_FOUND)));
        }

        // 좋아요/싫어요 내역 조회
        Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(tempUser.getId(),
                festivalId, ticketingId, reviewId);
        if(findLikes != 0){
            throw new CustomException(CustomErrorCode.LIKES_ALREADY_EXISTS);
        }

        // 좋아요/싫어요 저장
        LikeOrDislike likes = request.toEntity(tempUser, festival, ticketing, review);
        likeOrDislikeRepository.save(likes);
    }
}
