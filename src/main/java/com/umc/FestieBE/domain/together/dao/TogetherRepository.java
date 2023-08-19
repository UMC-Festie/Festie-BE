package com.umc.FestieBE.domain.together.dao;

import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.global.type.CategoryType;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TogetherRepository extends JpaRepository<Together, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Together t SET t.view = t.view + 1 " +
            "WHERE t.id = :togetherId")
    void updateView(@Param("togetherId") Long togetherId);

    @Query("SELECT t FROM Together t " +
            "JOIN FETCH t.user u " +
            "WHERE t.id = :togetherId")
    Optional<Together> findByIdWithUser(@Param("togetherId") Long togetherId);

    @Transactional
    @Modifying
    @Query("UPDATE Together t SET t.status = 1 " +
            "WHERE t.id = :togetherId")
    void updateStatusMatched(@Param("togetherId") Long togetherId);

    @Transactional
    @Modifying
    @Query("UPDATE Together t SET t.status = 1 " +
            "where t.status = 0 AND t.date < :currentDate")
    void updateStatusMatchedAutomatically(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT t FROM Together t " +
            "JOIN t.user u " +
            "WHERE (:type IS NULL OR t.type = :type) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:region IS NULL OR t.region = :region) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN t.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN t.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN t.view END DESC, t.createdAt DESC, " + // 조회 많은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN t.view END ASC, t.createdAt DESC") // 조회 적은 순
    Slice<Together> findAllTogether(PageRequest pageRequest,
                                    @Param("type") FestivalType festivalType,
                                    @Param("category") CategoryType categoryType,
                                    @Param("region") RegionType regionType,
                                    @Param("status") Integer status,
                                    @Param("sortBy") String sort);

    @Query("SELECT COUNT(t) FROM Together t " +
            "WHERE (:type IS NULL OR t.type = :type) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:region IS NULL OR t.region = :region) " +
            "AND (:status IS NULL OR t.status = :status) ")
    long countTogether(@Param("type") FestivalType festivalType,
                       @Param("category") CategoryType category,
                       @Param("region") RegionType regionType,
                       @Param("status") Integer status);

    @Query("SELECT t FROM Together t " +
            "JOIN t.user u " +
            "WHERE :status IS NULL OR t.status = :status")
    Page<Together> findAllWithUser(Pageable pageable, @Param("status") Integer status);

    @Query("SELECT t FROM Together t " +
            "WHERE t.title LIKE %:keyword% OR t.content LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN t.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN t.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN t.view END DESC, t.createdAt DESC, " + // 조회 높은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN t.view END ASC, t.createdAt DESC") // 조회 낮은 순
    Page<Together> findByTitleAndContent(PageRequest pageRequest,
                                         @Param("keyword") String keyword,
                                         @Param("sortBy") String sort);

    @Query("SELECT t FROM Together t " +
            "WHERE t.title LIKE %:keyword% OR t.content LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN t.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '오래된순' THEN t.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '조회높은순' THEN t.view END DESC, t.createdAt DESC, " + // 조회 높은 순
            "CASE WHEN :sortBy = '조회낮은순' THEN t.view END ASC, t.createdAt DESC") // 조회 낮은 순
    List<Together> findByTitleAndContent(@Param("keyword") String keyword,
                                         @Param("sortBy") String sort);

}