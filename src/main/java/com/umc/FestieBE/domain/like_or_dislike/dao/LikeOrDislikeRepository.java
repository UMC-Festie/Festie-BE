package com.umc.FestieBE.domain.like_or_dislike.dao;

import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeOrDislikeRepository extends JpaRepository<LikeOrDislike,Long> {

    @Query("SELECT COUNT(ld) FROM LikeOrDislike ld " +
            "WHERE (ld.festival.id = :festivalId OR :festivalId IS NULL) " +
            "AND (ld.ticketing.id = :ticketingId OR :ticketingId IS NULL) " +
            "AND (ld.review.id = :reviewId OR :reviewId IS NULL) " +
            "AND (ld.openperformance.id = :openperformanceId OR :openperformanceId IS NULL) " +
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
            "AND (ld.openperformance.id = :openperformanceId OR :openperformanceId IS NULL)")
            //"AND ld.user.id = :userId") 유저
            // "AND ld.temporaryUser.id = :userId")
    Long findByTargetIdTestWithStatus(@Param("status") Integer status,
                                      @Param("festivalId") Long festivalId,
                                      @Param("ticketingId") Long ticketingId,
                                      @Param("reviewId") Long reviewId,
                                      @Param("openperformanceId") String openperformanceId);

}
