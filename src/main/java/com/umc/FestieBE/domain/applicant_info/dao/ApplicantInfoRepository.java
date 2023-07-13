package com.umc.FestieBE.domain.applicant_info.dao;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantInfoRepository extends JpaRepository<ApplicantInfo, Long> {
}
