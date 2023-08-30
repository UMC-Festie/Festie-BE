package com.umc.FestieBE.domain.mypage.dto;

import com.umc.FestieBE.domain.mypage.domain.Mypage;
import lombok.Getter;

public class MypageResponseDTO {
    @Getter
    public static class MypageUserResponse {
        private String email;
        private String nickname;
        private String gender;
        private String birth;


        public MypageUserResponse(Mypage mypage) {
            this.email = mypage.getEmail();
            this.nickname = mypage.getNickname();
            this.gender = mypage.getGender();
            this.birth = mypage.getBirth();
        }
    }
}
