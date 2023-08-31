package com.umc.FestieBE.domain.applicant_info.dao;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.together.domain.Together;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApplicantInfoRepository extends JpaRepository<ApplicantInfo, Long> {

    @Query("SELECT ai FROM ApplicantInfo ai " +
            "JOIN FETCH ai.user u " +
            "WHERE ai.together.id = :togetherId " +
            "ORDER BY ai.createdAt desc")
    List<ApplicantInfo> findByTogetherIdWithUser(@Param("togetherId") Long togetherId);


    @Query("SELECT ai FROM ApplicantInfo ai " +
            "WHERE ai.together.id = :togetherId AND ai.user.id = :userId")
    Optional<ApplicantInfo> findByTogetherIdAndUserId(@Param("togetherId") Long togetherId, @Param("userId") Long userId);

    List<ApplicantInfo> findByTogether(Together together);

    @Transactional
    @Modifying
    @Query("UPDATE ApplicantInfo ai SET ai.isSelected = true " +
            "WHERE ai.together.id = :togetherId AND " +
            "ai.user.id IN (:bestieIdList)")
    void updateStatus(@Param("togetherId") Long togetherId,
                      @Param("bestieIdList") List<Long> bestieIdList);

    @Transactional
    void deleteByTogetherId(@Param("togetherId") Long togetherId);

    @Query("SELECT ai.isSelected FROM ApplicantInfo ai " +
            "WHERE ai.together.id = :togetherId AND ai.user.id = :userId")
    Boolean findStatusByTogetherIdAndUserId(@Param("togetherId") Long togetherId, @Param("userId") Long userId);

    Page<ApplicantInfo> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
}