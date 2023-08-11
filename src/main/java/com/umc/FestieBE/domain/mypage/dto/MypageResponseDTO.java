package com.umc.FestieBE.domain.mypage.dto;

import com.umc.FestieBE.domain.mypage.domain.Mypage;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class MypageResponseDTO {
    @Getter
    public static class MypageUserResponse {
        private String email;
        private String nickname;
        private String gender;
        private Integer age;

        private List<TicketingResponseDTO.TicketingRecentResponse> recentTicketings;


        public MypageUserResponse(Mypage mypage, List<Ticketing> recentTicketings) {
            this.email = mypage.getEmail();
            this.nickname = mypage.getNickname();
            this.gender = mypage.getGender();
            this.age = mypage.getAge();

            this.recentTicketings = recentTicketings.stream()
                    .map(TicketingResponseDTO.TicketingRecentResponse::new)
                    .collect(Collectors.toList());
        }
    }
}
