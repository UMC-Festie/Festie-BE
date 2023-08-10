package com.umc.FestieBE.domain.open_festival.dao;

import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

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
}
