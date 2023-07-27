package com.umc.FestieBE.domain.together.dao;

import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.global.type.FestivalType;
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
import java.util.Optional;

import java.util.Optional;

public interface TogetherRepository extends JpaRepository<Together, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Together t SET t.view = t.view + 1 " +
            "WHERE t.id = :togetherId")
    void updateView(@Param("togetherId") Long togetherId);

    @Query("SELECT t FROM Together t " +
            "JOIN FETCH t.temporaryUser u " + //임시 유저
            "WHERE t.id = :togetherId")
    Optional<Together> findByIdWithUser(@Param("togetherId") Long togetherId);

    @Transactional
    @Modifying
    @Query("UPDATE Together t SET t.status = 1 " +
            "WHERE t.id = :togetherId")
    void updateStatusMatched(@Param("togetherId") Long togetherId);


    @Query("SELECT t FROM Together t " +
            "JOIN t.temporaryUser u " +
            "WHERE (:type IS NULL OR t.type = :type) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:region IS NULL OR t.region = :region) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '0' THEN t.createdAt END DESC, " + // 최신 순
            "CASE WHEN :sortBy = '1' THEN t.createdAt END ASC, " + // 오래된 순
            "CASE WHEN :sortBy = '2' THEN t.view END DESC, t.createdAt DESC, " + // 조회 많은 순
            "CASE WHEN :sortBy = '3' THEN t.view END ASC, t.createdAt DESC") // 조회 적은 순
    Slice<Together> findAllTogether(PageRequest pageRequest,
                                    @Param("type") String festivalType,
                                    @Param("category") String categoryType,
                                    @Param("region") String regionType,
                                    @Param("status") Integer status,
                                    @Param("sortBy") String sort);

    @Query("SELECT COUNT(t) FROM Together t " +
            "WHERE (:type IS NULL OR t.type = :type) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:region IS NULL OR t.region = :region) " +
            "AND (:status IS NULL OR t.status = :status) ")
    long countTogether(@Param("type") String festivalType,
                       @Param("category") String category,
                       @Param("region") String regionType,
                       @Param("status") Integer status);

}