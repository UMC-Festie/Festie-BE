package com.umc.FestieBE.domain.festival.dao;


import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
            "JOIN FETCH f.user u " +
            "WHERE f.id = :festivalId")
    Optional<Festival> findByIdWithUser(@Param("festivalId") Long festivalId);


    // 공연/축제 정보 검색
    @Query("SELECT f FROM Festival f " +
            "WHERE f.festivalTitle LIKE %:keyword% " +
            "ORDER BY f.createdAt DESC")
    List<Festival> findByFestivalTitleContaining(@Param("keyword") String keyword);

    // 홈 화면 - 곧 다가와요
    @Query("SELECT f FROM Festival f " +
            "WHERE f.type = :festivalType AND f.endDate > :currentDate " +
            "ORDER BY f.startDate ASC, f.view DESC")
    List<Festival> findTop4ByStartDateAndView(@Param("currentDate") LocalDate currentDate,
                                              @Param("festivalType") FestivalType festivalType);

    @Query("SELECT f FROM Festival f " +
            "WHERE (:category IS NULL OR f.category = :category) " +
            "AND (:region IS NULL OR f.region = :region) " +
            "AND (:duration IS NULL OR " +
            "      (:duration = '공연중' AND f.duration = '공연중') OR " +
            "      (:duration = '공연완료' AND f.duration = '공연완료') OR " +
            "      (:duration = '공연예정' AND f.duration = '공연예정') OR " +
            "      (:duration = '축제중' AND f.duration = '축제중') OR " +
            "      (:duration = '축제완료' AND f.duration = '축제완료') OR " +
            "      (:duration = '축제예정' AND f.duration = '축제예정')) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN f.id END DESC, " +
            "CASE WHEN :sortBy = '오래된순' THEN f.id END ASC, " +
            "CASE WHEN :sortBy = '조회높은순' THEN f.view END DESC, " +
            "CASE WHEN :sortBy = '조회낮은순' THEN f.view END ASC, " +
            "CASE WHEN :sortBy = '좋아요많은순' THEN f.likes END DESC, " +
            "CASE WHEN :sortBy = '좋아요적은순' THEN f.likes END ASC, " +
            "f.id DESC") // 기본적으로 최신순으로 정렬
    Page<Festival> findAllFestival(
            @Param("sortBy") String sortBy,
            @Param("category") CategoryType category,
            @Param("region") RegionType region,
            @Param("duration") String duration,
            PageRequest pageRequest
    );

    // 통합검색
    @Query("SELECT f FROM Festival f " +
            "WHERE f.title LIKE %:keyword% OR f.content LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN f.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN f.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN f.view END DESC, f.createdAt DESC, " + // 조회 높은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN f.view END ASC, f.createdAt DESC") // 조회 낮은 순
    Page<Festival> findByTitleAndContent(PageRequest pageRequest,
                                         @Param("keyword") String keyword,
                                         @Param("sortBy") String sort);

    @Query("SELECT f FROM Festival f " +
            "WHERE f.title LIKE %:keyword% OR f.content LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN f.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN f.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN f.view END DESC, f.createdAt DESC, " + // 조회 높은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN f.view END ASC, f.createdAt DESC") // 조회 낮은 순
    List<Festival> findByTitleAndContent(@Param("keyword") String keyword,
                                         @Param("sortBy") String sort);
}
