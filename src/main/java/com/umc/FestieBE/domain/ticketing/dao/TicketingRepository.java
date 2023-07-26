package com.umc.FestieBE.domain.ticketing.dao;

import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.together.domain.Together;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// @Repository
public interface TicketingRepository extends JpaRepository<Ticketing, Long> {

    // 조회수
    @Transactional
    @Modifying
    @Query("UPDATE Ticketing t SET t.view = t.view + 1 " +
            "WHERE t.id = :ticketingId")
    void updateView(@Param("ticketingId") Long ticketingId);

    // 임시 유저
    @Query("SELECT t FROM Ticketing t " +
            "JOIN FETCH t.temporaryUser u " + //임시 유저
            "WHERE t.id = :ticketingId")
    Optional<Ticketing> findByIdWithUser(@Param("ticketingId") Long ticketingId);

}
