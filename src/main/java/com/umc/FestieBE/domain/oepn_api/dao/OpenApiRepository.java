package com.umc.FestieBE.domain.oepn_api.dao;

import com.umc.FestieBE.domain.oepn_api.domain.OpenApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenApiRepository extends JpaRepository<OpenApi, Long> {
}
