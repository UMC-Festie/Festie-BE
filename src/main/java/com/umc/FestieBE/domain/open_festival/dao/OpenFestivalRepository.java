package com.umc.FestieBE.domain.open_festival.dao;

import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OpenFestivalRepository extends JpaRepository<OpenFestival, Long> {
    //@Query("SELECT of FROM OpenFestival of " +
    //        "WHERE CAST(of.endDate AS java.time.LocalDate) > :currentDate " +
    //        "ORDER BY CAST(of.startDate AS java.time.LocalDate) ASC, of.view DESC")
    @Query("SELECT of FROM OpenFestival of " +
            "WHERE CAST(of.endDate AS java.time.LocalDate) > :currentDate " +
            "ORDER BY CAST(of.startDate AS java.time.LocalDate) ASC")
    Page<OpenFestival> findAll(Pageable pageable, @Param("currentDate") LocalDate currentDate);
}
