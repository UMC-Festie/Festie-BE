package com.umc.FestieBE.domain.festival.dao;


import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.global.type.FestivalType;
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
            "JOIN FETCH f.temporaryUser u " +
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
}
