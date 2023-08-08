package com.umc.FestieBE.domain.open_festival.dao;

import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenFestivalRepository extends JpaRepository<OpenFestival, Long> {
}
