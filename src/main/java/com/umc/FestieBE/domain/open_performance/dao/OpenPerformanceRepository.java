package com.umc.FestieBE.domain.open_performance.dao;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OpenPerformanceRepository extends JpaRepository<OpenPerformance, Long> {

    //조회수
    @Transactional
    @Modifying
    @Query("UPDATE OpenPerformance p SET p.view = p.view + 1" +
            "WHERE p.id = :openPerformanceId")
    void updateView(@Param("openPerformanceId") String openPerformanceId);

    //공연 목록 조회
    @Query("SELECT p FROM OpenPerformance p " +
            "WHERE (:category IS NULL OR p.category = :category" +
            "AND (:region IS NULL OR p.region = :region)" +
            "AND (p.status IS NULL OR p.status = :status" +
            "ORDER BY" +
            "CASE WHEN :sortBy = '최신순' THEN p.createAt END DESC, " + //최신순
            "CASE WHEN :sortBy = '오래된순' THEN p.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회많은순' THEN p.view END DESC, " + // 조회 많은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN p.view END ASC," +// 조회 적은 순
            "CASE WHEN :sortBy = '좋아요많은순' THEN p.likes END DESC,"  +
            "CASE WHEN :sortBy = '좋아요낮은순' THEN p.likes END ASC"
    )
    Slice<OpenPerformance> findAllPerformance(PageRequest pageRequest,
                                              @Param("category")CategoryType category,
                                              @Param("sortBy") String sortBy,
                                              @Param("region") RegionType region,
                                              @Param("duration") String duration
    );

    //총 수
    @Query("SELECT COUNT(p) FROM OpenPerformance p" +
            "WHERE (:type IS NULL OR p.type = :type) " +
            "AND (:category IS NULL OR p.category = :category)" +
            "AND (:region IS NULL OR p.region = :region)" +
            "AND (p.status IS NULL OR p.status = :status")
    long countTogether(@Param("category")CategoryType category,
                       @Param("region") RegionType region,
                       @Param("duration") String duration);


}
