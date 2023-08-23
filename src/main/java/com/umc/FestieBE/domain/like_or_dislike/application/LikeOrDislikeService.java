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
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
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

    public void createLikeOrDislike(LikeOrDislikeRequestDTO request) {
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Long festivalId = request.getFestivalId();
        Long ticketingId = request.getTicketingId();
        Long reviewId = request.getReviewId();
        String openperformanceId = request.getOpenperformanceId();
        String openfestivalId = request.getOpenfestivalId();

        if (festivalId == null && ticketingId == null && reviewId == null && openperformanceId == null && openfestivalId == null) {
            throw new CustomException(LIKES_TARGET_NOT_FOUND);
        }

        int status = request.getStatus();

        if (festivalId != null) {
            processLikeOrDislike(request, user, status);
        } else if (ticketingId != null) {
            processLikeOrDislike(request, user, status);
        } else if (reviewId != null) {
            // processReviewLikeOrDislike(user, reviewId, status);
            // TODO: Review에 대한 처리 추가
        } else if (openperformanceId != null) {
             processLikeOrDislike(request,user, status);
        } else if (openfestivalId != null) {
            processLikeOrDislike(request,user,status);
        }
    }

    private void processLikeOrDislike(LikeOrDislikeRequestDTO request, User user, int status) {
        Long festivalId = request.getFestivalId();
        Long ticketingId = request.getTicketingId();
        String openperformanceId = request.getOpenperformanceId();
        String openfestivalId = request.getOpenfestivalId();

        if (festivalId != null) { // [새로운 공연, 축제]
            Festival festival = festivalRepository.findById(festivalId)
                    .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

            LikeOrDislike likeOrDislikes = likeOrDislikeRepository.findByUserIdAndFestivalId(user.getId(), festivalId);

            if (likeOrDislikes == null) { // 좋아요/싫어요를 누른 적이 없는 경우
                if (status == 1) {
                    festival.incrementLikes();
                } else if (status == 0) {
                    festival.incrementDislikes();
                }
                LikeOrDislike likes = request.toEntity(user, festival, null, null, null,null);
                likeOrDislikeRepository.save(likes);
            } else { // 이미 좋아요/싫어요를 누른 경우
                if (status == 1) {
                    festival.decrementLikes();
                } else if (status == 0) {
                    festival.decrementDislikes();
                }
                likeOrDislikeRepository.delete(likeOrDislikes);
            }
            festivalRepository.save(festival);
        }
        else if (ticketingId != null) { // [티켓팅]
            Ticketing ticketing = ticketingRepository.findById(ticketingId)
                    .orElseThrow(() -> new CustomException(TICKETING_NOT_FOUND));

            LikeOrDislike likeOrDislikes = likeOrDislikeRepository.findByUserIdAndTicketingId(user.getId(), ticketingId);

            if (likeOrDislikes == null) {
                if (status == 1) {
                    ticketing.incrementLikes();
                } else if (status == 0) {
                    ticketing.incrementDislikes();
                }
                LikeOrDislike likes = request.toEntity(user, null, ticketing, null, null,null);
                likeOrDislikeRepository.save(likes);
            } else {
                if (status == 1) {
                    ticketing.decrementLikes();
                } else if (status == 0) {
                    ticketing.decrementDislikes();
                }
                likeOrDislikeRepository.delete(likeOrDislikes);
            }
            ticketingRepository.save(ticketing);
        }
        else if (festivalId != null) { // [후기]
            // TODO 후기 로직 작성
        }
        else if (openperformanceId != null) {
            OpenPerformance openperformance = openPerformanceRepository.findById(openperformanceId)
                    .orElseThrow(() -> new CustomException(OPEN_NOT_FOUND));

            LikeOrDislike likeOrDislike = likeOrDislikeRepository.findByUserIdAndOpenPerformanceId(user.getId(), openperformanceId);

            if(likeOrDislike == null){ //좋아요or싫어요를 누른 적이 없는 경우
                if (status ==1){
                    openperformance.incrementLikes(); //좋아요
                }else if (status ==0){
                    openperformance.incrementDislikes(); //싫어요
                }
                LikeOrDislike likes = request.toEntity(user, null,null,null,openperformance,null);
                likeOrDislikeRepository.save(likes);

            }else { //이미 좋아요or싫어요를 누른 경우
                if (status ==1){
                    openperformance.decrementLikes();
                } else if (status ==0) {
                    openperformance.decrementLikes();
                }
                likeOrDislikeRepository.delete(likeOrDislike);
            }
            openPerformanceRepository.save(openperformance);

        }else if(openfestivalId !=null){
            OpenFestival openfestival = openFestivalRepository.findById(openfestivalId)
                    .orElseThrow(() -> new CustomException(OPEN_NOT_FOUND));

            LikeOrDislike likeOrDislike = likeOrDislikeRepository.findByUserIdAndOpenFestivalId(user.getId(), openfestivalId);

            if (likeOrDislike == null){
                if (status ==1){
                    openfestival.incrementLikes();
                } else if (status == 0) {
                    openfestival.incrementDislikes();
                }
                LikeOrDislike likes = request.toEntity(user,null,null,null,null,openfestival);
                likeOrDislikeRepository.save(likes);
            }else {
                if (status == 1){
                    openfestival.decrementLikes();
                }else if (status ==0){
                    openfestival.decrementDislikes();
                }
                likeOrDislikeRepository.delete(likeOrDislike);
            }
            openFestivalRepository.save(openfestival);
        }
    }
}