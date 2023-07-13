package com.umc.FestieBE.domain.festival.dao;


import com.umc.FestieBE.domain.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
