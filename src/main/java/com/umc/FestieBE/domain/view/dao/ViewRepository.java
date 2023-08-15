package com.umc.FestieBE.domain.view.dao;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.view.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ViewRepository extends JpaRepository<View, Long> {

    //조회수 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE View v SET v.view = v.view + 1 " +
            "WHERE v.openperformance.id = :openperformanceId")
    void updateView(@Param("openperformanceId") String openperformanceId);

    //조회수 id에 따른 viewCount
    @Query("SELECT v.view FROM View v " +
            "WHERE v.openperformance.id = :openperformanceId")
    Long findByIdWithCount(@Param("openperformanceId") String openperformanceId);

    @Query("SELECT v FROM View v " +
            "WHERE v.openperformance.id = :openperformanceId")
    View findByOpenperformance(@Param("openperformanceId") String openperformanceId);

}
