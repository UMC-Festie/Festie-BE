package com.umc.FestieBE.domain.open_performance.dao;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenPerformanceRepository extends JpaRepository<OpenPerformance, Long> {
}
