package com.umc.FestieBE.domain.ticketing.dao;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.together.domain.Together;
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
}
