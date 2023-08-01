package com.umc.FestieBE.domain.festival.dao;


import com.umc.FestieBE.domain.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
}
