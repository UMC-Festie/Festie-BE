package com.umc.FestieBE.domain.ticketing.dao;

import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.RegionType;
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

// @Repository
public interface TicketingRepository extends JpaRepository<Ticketing, Long> {

    // 조회수
    @Transactional
    @Modifying
    @Query("UPDATE Ticketing t SET t.view = t.view + 1 " +
            "WHERE t.id = :ticketingId")
    void updateView(@Param("ticketingId") Long ticketingId);


    @Query("SELECT t FROM Ticketing t " +
            "JOIN FETCH t.user u " +
            "WHERE t.id = :ticketingId")
    Optional<Ticketing> findByIdWithUser(@Param("ticketingId") Long ticketingId);

    @Query("SELECT t FROM Ticketing t " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN t.id END DESC, " +
            "CASE WHEN :sortBy = '오래된순' THEN t.id END ASC, " +
            "CASE WHEN :sortBy = '조회높은순' THEN t.view END DESC, " +
            "CASE WHEN :sortBy = '조회낮은순' THEN t.view END ASC, " +
            "CASE WHEN :sortBy = '좋아요많은순' THEN t.likes END DESC, " +
            "CASE WHEN :sortBy = '좋아요적은순' THEN t.likes END ASC, " +
            "t.id DESC") // 기본적으로 최신순으로 정렬
    Page<Ticketing> findAllTicketing (
            @Param("sortBy") String sortBy,
            Pageable pageRequest
    );

    // 통합검색
    @Query("SELECT t FROM Ticketing t " +
            "WHERE t.title LIKE %:keyword% OR t.content LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN t.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN t.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN t.view END DESC, t.createdAt DESC, " + // 조회 많은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN t.view END ASC, t.createdAt DESC") // 조회 적은 순
    Page<Ticketing> findByTitleAndContent(PageRequest pageRequest,
                                          @Param("keyword") String keyword,
                                          @Param("sortBy") String sort);

    @Query("SELECT t FROM Ticketing t " +
            "WHERE t.title LIKE %:keyword% OR t.content LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN t.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN t.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN t.view END DESC, t.createdAt DESC, " + // 조회 많은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN t.view END ASC, t.createdAt DESC") // 조회 적은 순
    List<Ticketing> findByTitleAndContent(@Param("keyword") String keyword,
                                          @Param("sortBy") String sort);
}
