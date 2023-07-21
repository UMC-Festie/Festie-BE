package com.umc.FestieBE.domain.ticketing.dao;

import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository
public interface TicketingRepository extends JpaRepository<Ticketing, Long> {

}
