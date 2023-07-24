package com.umc.FestieBE.domain.applicant_info.dto;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;

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
        //User user = applicantInfo.getUser();
        TemporaryUser tempUser = applicantInfo.getTemporaryUser();

        // User 만 나이 계산
        //LocalDate currentDate = LocalDate.now();
        //int age = Period.between(user.getBirthday(), currentDate).getYears();

        this.userId = tempUser.getId();
        this.nickname = tempUser.getNickname();
        this.age = tempUser.getAge(); // user.age -> birthday 컬럼 변경 시 그냥 age 로 업데이트 필요
        this.gender = String.valueOf(tempUser.getGender());
        this.introduction = applicantInfo.getIntroduction();
    }

}
