package com.umc.FestieBE.domain.together.dao;

import com.umc.FestieBE.domain.together.domain.Together;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TogetherRepository extends JpaRepository<Together, Long> {

    @Transactional
    @Modifying
    @Query(value = "update Together t set t.view = t.view + 1 " +
            "where t.id = :togetherId")
    void updateView(@Param("togetherId") Long togetherId);

    @Query(value = "select t from Together t " +
            "join fetch t.temporaryUser u " + //임시 유저
            "where t.id = :togetherId")
    Optional<Together> findByIdWithUser(@Param("togetherId") Long togetherId);

    @Transactional
    @Modifying
    @Query("UPDATE Together t SET t.status = 1 " +
            "WHERE t.id = :togetherId")
    void updateStatusMatched(@Param("togetherId") Long togetherId);

}
