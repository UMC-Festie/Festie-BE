package com.umc.FestieBE.domain.festival.dao;


import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    // 조회수
    @Transactional
    @Modifying
    @Query("UPDATE Festival f SET f.view = f.view + 1 " +
            "WHERE f.id = :festivalId")
    void updateView(@Param("festivalId") Long festivalID);

    // 임시 유저
    @Query("SELECT f FROM Festival f " +
            "JOIN FETCH f.temporaryUser u " +
            "WHERE f.id = :festivalId")
    Optional<Festival> findByIdWithUser(@Param("festivalId") Long festivalId);

    @Query("SELECT f FROM Festival f " +
            "WHERE f.id < :lastFestivalId " +
            "AND (:category IS NULL OR f.category = :category) " +
            "AND (:region IS NULL OR f.region = :region) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'LATEST' THEN f.id END DESC, " +
            "CASE WHEN :sortBy = 'OLDEST' THEN f.id END ASC, " +
            "CASE WHEN :sortBy = 'MOST_VIEWED' THEN f.view END DESC, " +
            "CASE WHEN :sortBy = 'LEAST_VIEWED' THEN f.view END ASC, " +
            "f.id DESC") // 기본적으로 최신순으로 정렬
    Page<Festival> findAllTogether(
            @Param("lastFestivalId") Long lastFestivalId,
            @Param("sortBy") String sortBy,
            @Param("category") CategoryType category,
            @Param("region") RegionType region,
            PageRequest pageRequest
    );
}
