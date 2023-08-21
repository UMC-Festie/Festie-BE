package com.umc.FestieBE.domain.open_performance.dao;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.DurationType;
import com.umc.FestieBE.global.type.FestivalType;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OpenPerformanceRepository extends JpaRepository<OpenPerformance, Long> {

    @Query("SELECT COUNT(p) FROM OpenPerformance p " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:region IS NULL OR p.region = :region) " +
            "AND (:duration IS NULL OR p.duration = :duration) ")
    long countTogether(@Param("category") CategoryType category,
                       @Param("region") RegionType regionType,
                       @Param("duration") DurationType durationType);

    //목록조회
    @Query("SELECT p FROM OpenPerformance p " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:region IS NULL OR p.region = :region) " +
            "AND (:duration IS NULL OR p.duration = :duration) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN p.startDate END DESC, " +
            "CASE WHEN :sortBy = '오래된순' THEN p.startDate END ASC, " +
            "CASE WHEN :sortBy = '좋아요많은순' THEN p.likes END DESC, " +
            "CASE WHEN :sortBy = '좋아요적은순' THEN p.likes END ASC, " +
            "CASE WHEN :sortBy = '조회높은순' THEN p.view END DESC, p.startDate DESC, " +
            "CASE WHEN :sortBy = '조회낮은순' THEN p.view END ASC, " +
            "p.startDate DESC")
    Slice<OpenPerformance> findAllPerformance(PageRequest pageRequest,
                                              @Param("category") CategoryType category,
                                              @Param("region") RegionType region,
                                              @Param("duration") DurationType duration,
                                              @Param("sortBy") String sortBy);

    //OpenPerformance findById(String Id);


    // 홈화면 - 곧 다가와요
    // TODO 조회수 정렬 추가
    //@Query("SELECT op FROM OpenPerformance op " +
    //        "WHERE CAST(op.endDate AS java.time.LocalDate) > :currentDate " +
    //        "ORDER BY CAST(op.startDate AS java.time.LocalDate) ASC, op.view DESC")
    @Query("SELECT p FROM OpenPerformance p " +
            //"WHERE op.endDate > :currentDate " +
            //"WHERE TRIM(p.state) = '공연예정' " +
            "WHERE p.duration = 'WILL' " +
            "ORDER BY p.startDate ASC")
    Page<OpenPerformance> findByState(Pageable pageable, @Param("currentDate") LocalDate currentDate);

    // 공연/축제 연동 시 검색
    @Query("SELECT p FROM OpenPerformance p " +
            "WHERE p.festivalTitle LIKE %:keyword% "
            //"ORDER BY f.createdAt DESC"
    )
    List<OpenPerformance> findByFestivalTitleContaining(@Param("keyword") String keyword);

    @Query("SELECT p FROM OpenPerformance p " +
            "WHERE p.id = :id")
    Optional<OpenPerformance> findById(@Param("id") String id);

    // 통합검색
    @Query("SELECT distinct p FROM OpenPerformance p " +
            //"LEFT JOIN p.likeOrDislikes ld " +
            //"LEFT JOIN p.views v " +
            "WHERE p.festivalTitle LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN p.startDate END DESC, " + // 최신 순(startDate 기준)
            "CASE WHEN :sortBy = '오래된순' THEN p.startDate END ASC"  // 오래된 순(startDate 기준)
            //"CASE WHEN :sortBy = '조회많은순' THEN p.view END DESC, p.createdAt DESC, " + // 조회 많은 순
            //"CASE WHEN :sortBy = '조회적은순' THEN p.view END ASC, p.createdAt DESC" // 조회 적은 순
    )
    Page<OpenPerformance> findByTitle(PageRequest pageRequest,
                                      @Param("keyword") String keyword,
                                      @Param("sortBy") String sort);

    @Query("SELECT distinct p FROM OpenPerformance p " +
            //"LEFT JOIN FETCH p.likeOrDislikes ld " +
            //"LEFT JOIN FETCH p.views v " +
            "WHERE p.festivalTitle LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN p.startDate END DESC, " + // 최신 순(startDate 기준)
            "CASE WHEN :sortBy = '오래된순' THEN p.startDate END ASC"  // 오래된 순(startDate 기준)
            //"CASE WHEN :sortBy = '조회많은순' THEN p.view END DESC, p.createdAt DESC, " + // 조회 많은 순
            //"CASE WHEN :sortBy = '조회적은순' THEN p.view END ASC, p.createdAt DESC" // 조회 적은 순
    )
    List<OpenPerformance> findByTitle(@Param("keyword") String keyword,
                                      @Param("sortBy") String sort);

}
