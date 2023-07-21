package com.umc.FestieBE.domain.applicant_info.dao;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicantInfoRepository extends JpaRepository<ApplicantInfo, Long> {

    @Query("select ai from ApplicantInfo ai " +
            "join fetch ai.user u " +
            "where ai.together.id = :togetherId " +
            "order by ai.createdAt desc")
    List<ApplicantInfo> findByTogetherIdWithUser(@Param("togetherId") Long togetherId);

}
