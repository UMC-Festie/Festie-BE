package com.umc.FestieBE.domain.open_festival.dao;

import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OpenFestivalRepository extends JpaRepository<OpenFestival, Long> {

    // 홈화면 - 곧 다가와요
    // TODO 조회수 정렬 추가
    //@Query("SELECT of FROM OpenFestival of " +
    //        "WHERE CAST(of.endDate AS java.time.LocalDate) > :currentDate " +
    //        "ORDER BY CAST(of.startDate AS java.time.LocalDate) ASC, of.view DESC")
    @Query("SELECT f FROM OpenFestival f " +
            "WHERE TRIM(f.state) = '공연예정' " +
            "ORDER BY f.startDate ASC")
    Page<OpenFestival> findByState(Pageable pageable, @Param("currentDate") LocalDate currentDate);

    // 공연/축제 연동 시 검색
    @Query("SELECT f FROM OpenFestival f " +
            "WHERE f.festivalTitle LIKE %:keyword% "
            //"ORDER BY f.createdAt DESC"
    )
    List<OpenFestival> findByFestivalTitleContaining(@Param("keyword") String keyword);

    @Query("SELECT f FROM OpenFestival f " +
            "WHERE f.id = :id")
    Optional<OpenFestival> findById(@Param("id") String id);


    // 통합검색
    @Query("SELECT distinct f FROM OpenFestival f " +
            "LEFT JOIN FETCH f.likeOrDislikes ld " +
            "WHERE f.festivalTitle LIKE %:keyword% " +
            "ORDER BY " +
            "CASE WHEN :sortBy = '최신순' THEN f.startDate END DESC, " + // 최신 순(startDate 기준)
            "CASE WHEN :sortBy = '오래된순' THEN f.startDate END ASC"  // 오래된 순(startDate 기준)
            //"CASE WHEN :sortBy = '조회많은순' THEN p.view END DESC, p.createdAt DESC, " + // 조회 많은 순
            //"CASE WHEN :sortBy = '조회적은순' THEN p.view END ASC, p.createdAt DESC" // 조회 적은 순
    )
    List<OpenFestival> findByTitle(@Param("keyword") String keyword,
                                      @Param("sortBy") String sort);
}
