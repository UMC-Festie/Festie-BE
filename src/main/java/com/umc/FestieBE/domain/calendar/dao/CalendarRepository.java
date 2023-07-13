package com.umc.FestieBE.domain.calendar.dao;

import com.umc.FestieBE.domain.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

}
