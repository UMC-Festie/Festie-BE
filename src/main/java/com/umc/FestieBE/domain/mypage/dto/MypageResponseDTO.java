package com.umc.FestieBE.domain.mypage.dto;

import com.umc.FestieBE.domain.mypage.domain.Mypage;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
public class MypageResponseDTO {
    private String email;
    private String nickname;
    private String gender;
    private Integer age;

    public MypageResponseDTO(Mypage mypage) {
        this.email = mypage.getEmail();
        this.nickname = mypage.getNickname();
        this.gender = mypage.getGender();
        this.age = mypage.getAge();
    }
}
