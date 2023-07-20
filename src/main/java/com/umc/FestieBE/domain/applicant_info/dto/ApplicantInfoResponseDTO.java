package com.umc.FestieBE.domain.applicant_info.dto;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ApplicantInfoResponseDTO {
    private Long userId;
    private String nickname;
    private int age;
    private String introduction;

    // Entity -> DTO
    public ApplicantInfoResponseDTO(ApplicantInfo applicantInfo){
        User user = applicantInfo.getUser();

        // User 만 나이 계산
        //LocalDate currentDate = LocalDate.now();
        //int age = Period.between(user.getBirthday(), currentDate).getYears();

        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.age = user.getAge(); // user.age -> birthday 컬럼 변경 시 그냥 age 로 업데이트 필요
        this.introduction = applicantInfo.getIntroduction();
    }

}
