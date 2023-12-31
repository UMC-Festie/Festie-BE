package com.umc.FestieBE.domain.calendar.dao;

import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.together.domain.Together;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    @Query("SELECT c FROM Calendar c " +
            "JOIN FETCH c.user u " +
            "WHERE c.id = :calendarId")
    Optional<Calendar> findByIdWithUser(@Param("calendarId") Long calendarId);
}
