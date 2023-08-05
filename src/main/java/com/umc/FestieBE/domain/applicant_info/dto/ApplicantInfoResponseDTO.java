package com.umc.FestieBE.domain.applicant_info.dto;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
public class ApplicantInfoResponseDTO {
    private Long userId;
    private String nickname;
    private int age;
    private String gender;
    private String introduction;

    // Entity -> DTO
    public ApplicantInfoResponseDTO(ApplicantInfo applicantInfo){
        // 임시 유저
        User user = applicantInfo.getUser();

        // User 만 나이 계산
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(user.getBirthday(), currentDate).getYears();

        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.age = age;
        this.gender = String.valueOf(user.getGender());
        this.introduction = applicantInfo.getIntroduction();
    }

}
