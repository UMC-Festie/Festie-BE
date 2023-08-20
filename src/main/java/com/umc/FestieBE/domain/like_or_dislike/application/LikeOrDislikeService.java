package com.umc.FestieBE.domain.like_or_dislike.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.like_or_dislike.dto.LikeOrDislikeRequestDTO;
import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
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

import static com.umc.FestieBE.global.exception.CustomErrorCode.OPEN_NOT_FOUND;
import static com.umc.FestieBE.global.exception.CustomErrorCode.USER_NOT_FOUND;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;

import java.util.Optional;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;



@Service
@RequiredArgsConstructor
public class LikeOrDislikeService {
    private final FestivalRepository festivalRepository;
    private final TicketingRepository ticketingRepository;
    private final ReviewRepository reviewRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final OpenFestivalRepository openFestivalRepository;
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
        OpenFestival openfestival = null;

        Long festivalId = request.getFestivalId();
        Long ticketingId = request.getTicketingId();
        Long reviewId = request.getReviewId();
        String openperformanceId = request.getOpenperformanceId();
        String openfestivalId = request.getOpenfestivalId();

        if(festivalId == null && ticketingId == null && reviewId == null && openperformanceId == null && openfestivalId == null){
            throw new CustomException(CustomErrorCode.LIKES_TARGET_NOT_FOUND);
        }

        // 게시글 조회
        if(festivalId != null){
            festival = festivalRepository.findById(festivalId)
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));

            Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(),
                    festivalId, null, null, null, null);

            if(findLikes != 0) { // 이미 유저가 좋아요 또는 싫어요를 누른 상태라면,
                throw new CustomException(CustomErrorCode.LIKES_ALREADY_EXISTS); // 좋아요, 싫어요 반영 X
            } else {
                // Festival 테이블에 좋아요/싫어요 업뎃
                int status = request.getStatus();
                if (status == 1) { // 좋아요
                    festival.incrementLikes();
                } else if (status == 0) { // 싫어요
                    festival.incrementDislikes();
                }

                festivalRepository.save(festival);
            }
        }else if(ticketingId != null){
            ticketing = ticketingRepository.findById(ticketingId)
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.TICKETING_NOT_FOUND)));

            Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(),
                    null, ticketingId, null, null, null);

            if(findLikes != 0) { // 이미 유저가 좋아요 또는 싫어요를 누른 상태라면,
                throw new CustomException(CustomErrorCode.LIKES_ALREADY_EXISTS); // 좋아요, 싫어요 반영 X
            } else {
                // Festival 테이블에 좋아요/싫어요 업뎃
                int status = request.getStatus();
                if (status == 1) { // 좋아요
                    ticketing.incrementLikes();
                } else if (status == 0) { // 싫어요
                    ticketing.incrementDislikes();
                }

                ticketingRepository.save(ticketing);
            }
        }else if(reviewId != null){
            review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.REVIEW_NOT_FOUND)));
        }else if (openperformanceId != null) {
            //Optional<OpenPerformance> openPerformanceOptional = Optional.ofNullable(openPerformanceRepository.findById(openperformanceId));
            //if (openPerformanceOptional.isPresent()) {
            //    openperformance = openPerformanceOptional.get();
            //} else {
            //    throw new CustomException(CustomErrorCode.OPEN_NOT_FOUND);
            //}
            openperformance = openPerformanceRepository.findById(openperformanceId)
                    .orElseThrow(() -> new CustomException(OPEN_NOT_FOUND));
        }else if(openfestivalId !=null){
            openfestival = openFestivalRepository.findById(openfestivalId)
                    .orElseThrow(() -> new CustomException(OPEN_NOT_FOUND));
        }

        // 좋아요/싫어요 내역 조회
        Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(),
                festivalId, ticketingId, reviewId, openperformanceId, openfestivalId);

        if(findLikes != 0) {
            throw new CustomException(CustomErrorCode.LIKES_ALREADY_EXISTS);
        }

        // 좋아요/싫어요 저장
        LikeOrDislike likes = request.toEntity(user, festival, ticketing, review, openperformance, openfestival);
        likeOrDislikeRepository.save(likes);
    }


    /** 게시글 좋아요/싫어요 취소 */
    public void cancelLikeOrDislike(Long likeOrDislikedId) {
        LikeOrDislike likeOrDislike = likeOrDislikeRepository.findById(likeOrDislikedId)
                .orElseThrow(() -> new CustomException(LIKES_NOT_EXIST));

        // 유저 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != likeOrDislike.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "좋아요/싫어요 취소 권한이 없습니다.");
        }

        Long festivalId = likeOrDislike.getFestival() != null ? likeOrDislike.getFestival().getId() : null;
        Long ticketingId = likeOrDislike.getTicketing() != null ? likeOrDislike.getTicketing().getId() : null;
        Long reviewId = likeOrDislike.getReview() != null ? likeOrDislike.getReview().getId() : null;

        if(festivalId == null && ticketingId == null && reviewId == null) {
            throw new CustomException(CustomErrorCode.LIKES_TARGET_NOT_FOUND);
        }

        if(festivalId != null) { // [새로운 공연/축제]
            Festival festival = festivalRepository.findById(festivalId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));

            Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(), festivalId, null, null, null, null);

            if (findLikes == 0) { // 유저가 좋아요, 싫어요를 안누른 상태면,
                throw new CustomException(CustomErrorCode.LIKES_NOT_EXIST); // 취소할 좋아요, 싫어요값 없음
            } else {
                int status = likeOrDislike.getStatus();
                if (status == 1) {
                    festival.decrementLikes(); // 좋아요(1) 취소
                } else if (status == 0) {
                    festival.decrementDislikes(); // 싫어요(0) 취소
                }

                festivalRepository.save(festival);
            }
        }
        else if (ticketingId != null) { // [티켓팅]
            Ticketing ticketing = ticketingRepository.findById(ticketingId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.TICKETING_NOT_FOUND));

            Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(), null, ticketingId, null, null, null);

            if (findLikes == 0) {
                throw new CustomException(CustomErrorCode.LIKES_NOT_EXIST);
            } else {
                int status = likeOrDislike.getStatus();
                if (status == 1) {
                    ticketing.decrementLikes();
                } else if (status == 0) {
                    ticketing.decrementDislikes();
                }
                ticketingRepository.save(ticketing);
            }
        }
        else if (reviewId != null) { // [후기]
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.TICKETING_NOT_FOUND));

            Long findLikes = likeOrDislikeRepository.findByTargetIdAndUserId(user.getId(), null, null, reviewId, null, null);
        }

        // 좋아요/싫어요 데이터 삭제
        likeOrDislikeRepository.delete(likeOrDislike);
    }
}
