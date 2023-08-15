package com.umc.FestieBE.domain.like_or_dislike.dao;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_festival.dto.OpenFestivalDTO;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeOrDislikeRepository extends JpaRepository<LikeOrDislike,Long> {

    @Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
            "WHERE (ld.festival.id = :festivalId OR :festivalId IS NULL) " +
            "AND (ld.ticketing.id = :ticketingId OR :ticketingId IS NULL) " +
            "AND (ld.review.id = :reviewId OR :reviewId IS NULL) " +
            "AND (ld.openPerformance.id = :openperformanceId OR :openperformanceId IS NULL) " +
            "AND ld.user.id = :userId")
    Long findByTargetIdAndUserId(@Param("userId") Long userId,
                                 @Param("festivalId") Long festivalId,
                                 @Param("ticketingId")Long ticketingId,
                                 @Param("reviewId") Long reviewId,
                                 @Param("openperformanceId") String openperformanceId);


    // 좋아요, 싫어요 개수
    @Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
            "WHERE (ld.status = :status) " + // status가 1 또는 0인 경우
            "AND (ld.festival.id = :festivalId OR :festivalId IS NULL) " +
            "AND (ld.ticketing.id = :ticketingId OR :ticketingId IS NULL) " +
            "AND (ld.review.id = :reviewId OR :reviewId IS NULL) " +
            "AND (ld.openPerformance.id = :openperformanceId OR :openperformanceId IS NULL)")
            //"AND ld.user.id = :userId") 유저
            // "AND ld.temporaryUser.id = :userId")
    Long findByTargetIdTestWithStatus(@Param("status") Integer status,
                                      @Param("festivalId") Long festivalId,
                                      @Param("ticketingId") Long ticketingId,
                                      @Param("reviewId") Long reviewId,
                                      @Param("openperformanceId") String openperformanceId);

    //@Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
    //        "WHERE ld.festival = :festival AND ld.status = 1")
    //Long countLikesByFestival(@Param("festival") Festival festival);

    @Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
            "WHERE ld.review = :review AND ld.status = 1")
    Long countLikesByReview(@Param("review") Review review);

    @Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
            "WHERE ld.openPerformance = :openPerformance AND ld.status = 1")
    Long countLikesByOpenPerformance(@Param("openPerformance") OpenPerformance openPerformance);

    @Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
            "WHERE ld.openFestival = :openFestival AND ld.status = 1")
    Long countLikesByOpenFestival(@Param("openFestival") OpenFestival openFestival);
    //                                    @Param("reviewId") Long reviewId,
    //                                    @Param("openperformanceId") String openperformanceId);

    //좋아요, 싫어요 여부 //좋아요:0 싫어요:1
    @Query("SELECT CASE WHEN ld.status = 0 THEN 0 ELSE 1 END " +
            "FROM LikeOrDislike ld " +
            "WHERE (:festivalId IS NULL OR ld.festival.id = :festivalId) " +
            "AND (:ticketingId IS NULL OR ld.ticketing.id = :ticketingId) " +
            "AND (:reviewId IS NULL OR ld.review.id = :reviewId) " +
            "AND (:openperformanceId IS NULL OR ld.openPerformance.id = :openperformanceId) " +
            "AND ld.user.id = :userId")
    Long findLikeOrDislikeStatus(
            @Param("userId") Long userId,
            @Param("festivalId") Long festivalId,
            @Param("ticketingId") Long ticketingId,
            @Param("reviewId") Long reviewId,
            @Param("openperformanceId") String openperformanceId
    );

}
