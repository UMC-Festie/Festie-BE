package com.umc.FestieBE.domain.like_or_dislike.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.like_or_dislike.dto.LikeOrDislikeRequestDTO;
import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.dao.ReviewRepository;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserRepository;
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.umc.FestieBE.global.exception.CustomErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class LikeOrDislikeService {

    private final FestivalRepository festivalRepository;
    private final TicketingRepository ticketingRepository;
    private final ReviewRepository reviewRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 게시글 좋아요/싫어요
     */
    public void createLikeOrDislike(LikeOrDislikeRequestDTO request){

        // 유저
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Festival festival = null;
        Ticketing ticketing = null;
        Review review = null;
        OpenPerformance openperformance =null;

        Long festivalId = request.getFestivalId();
        Long ticketingId = request.getTicketingId();
        Long reviewId = request.getReviewId();
        String openperformanceId = request.getOpenperformanceId();

        if(festivalId == null && ticketingId == null && reviewId == null && openperformanceId == null){
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
        }else if (openperformanceId != null) {
            Optional<OpenPerformance> openPerformanceOptional = Optional.ofNullable(openPerformanceRepository.findById(openperformanceId));
            if (openPerformanceOptional.isPresent()) {
                openperformance = openPerformanceOptional.get();
            } else {
                throw new CustomException(CustomErrorCode.OPEN_NOT_FOUND);
            }
        }

        // 좋아요/싫어요 내역 조회
        Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(),
                festivalId, ticketingId, reviewId, openperformanceId);

        if(findLikes != 0){
            throw new CustomException(CustomErrorCode.LIKES_ALREADY_EXISTS);
        }

        // 좋아요/싫어요 저장
        LikeOrDislike likes = request.toEntity(user, festival, ticketing, review, openperformance);
        likeOrDislikeRepository.save(likes);
    }
}
