package com.umc.FestieBE.domain.open_performance.dao;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.DurationType;
import com.umc.FestieBE.global.type.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OpenPerformanceRepository extends JpaRepository<OpenPerformance, Long> {

    @Query("SELECT p FROM OpenPerformance p " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:region IS NULL OR p.region = :region) " +
            "AND (:duration IS NULL OR p.duration = :duration ) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN p.startDate END DESC, " +
            "CASE WHEN :sortBy = '오래된순' THEN p.startDate END ASC, " +
            "CASE WHEN :sortBy = '좋아요많은순' THEN p.likes END DESC, " +
            "CASE WHEN :sortBy = '좋아요적은순' THEN p.likes END ASC, " +
            "CASE WHEN :sortBy = '조회높은순' THEN p.view END DESC, p.startDate DESC, " +
            "CASE WHEN :sortBy = '조회낮은순' THEN p.view END ASC, " +
            "p.startDate DESC")
    Slice<OpenPerformance> findAllPerformance(
            PageRequest pageRequest,
            @Param("category") CategoryType category,
            @Param("region") RegionType region,
            @Param("duration") DurationType duration,
            @Param("sortBy") String sortBy
    );



}
