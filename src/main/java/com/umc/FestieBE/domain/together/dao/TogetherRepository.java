package com.umc.FestieBE.domain.together.dao;

import com.umc.FestieBE.domain.together.domain.Together;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TogetherRepository extends JpaRepository<Together, Long> {

    @Modifying
    @Query(value = "update Together t set t.view = t.view + 1 " +
            "where t.id = :togetherId")
    void updateView(@Param("togetherId") Long togetherId);

}
