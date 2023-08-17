package com.umc.FestieBE.domain.review.dao;

import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 통합검색
//    @Query("SELECT distinct r FROM Review r " +
//            "LEFT JOIN r.likesOrDislikes ld " +
//            "WHERE (r.title LIKE %:keyword% OR r.content LIKE %:keyword%) " +
//            "ORDER BY " +
//            "CASE WHEN :sortBy = '최신순' THEN r.createdAt END DESC, " + // 최신 순
//            "CASE WHEN :sortBy = '오래된순' THEN r.createdAt END ASC, " + // 오래된 순
//            "CASE WHEN :sortBy = '조회높은순' THEN r.view END DESC, r.createdAt DESC, " + // 조회 많은 순
//            "CASE WHEN :sortBy = '조회낮은순' THEN r.view END ASC, r.createdAt DESC") // 조회 적은 순
//    Page<Review> findByTitleAndContent(PageRequest pageRequest,
//                                       @Param("keyword") String keyword,
//                                       @Param("sortBy") String sort);
//
//    @Query("SELECT distinct r FROM Review r " +
//            "LEFT JOIN FETCH r.likeOrDislikes ld " +
//            "WHERE (r.title LIKE %:keyword% OR r.content LIKE %:keyword%) " +
//            "ORDER BY " +
//            "CASE WHEN :sortBy = '최신순' THEN r.createdAt END DESC, " + // 최신 순
//            "CASE WHEN :sortBy = '오래된순' THEN r.createdAt END ASC, " + // 오래된 순
//            "CASE WHEN :sortBy = '조회높은순' THEN r.view END DESC, r.createdAt DESC, " + // 조회 많은 순
//            "CASE WHEN :sortBy = '조회낮은순' THEN r.view END ASC, r.createdAt DESC") // 조회 적은 순
//    List<Review> findByTitleAndContent(@Param("keyword") String keyword,
//                                       @Param("sortBy") String sort);

    //조회 수
    @Transactional
    @Modifying
    @Query("UPDATE Review r SET r.view = r.view + 1 " +
            "WHERE r.id = :reviewId")
    void updateView(@Param("reviewId") Long reviewId);

    // 유저 조회
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.user u " +
            "WHERE r.id = :reviewId")
    Optional<Review> findByIdWithUser(@Param("reviewId") Long reviewId);


}
