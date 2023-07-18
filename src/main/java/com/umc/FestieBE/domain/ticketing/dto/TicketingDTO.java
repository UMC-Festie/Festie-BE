package com.umc.FestieBE.domain.ticketing.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

public class TicketingDTO {
    @Getter
    public static class TicketingRequest {
        @NotBlank(message = "티켓팅 제목은 필수 입력 값입니다.")
        private String title;
        @NotBlank(message = "티켓팅 작성 내용은 필수 입력 값입니다.")
        private String content;
        private Long view;

        private Long festivalId;
        private String festivalTitle;
        private String thumbnailUrl;

        @Min(value = 0, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
        @Max(value = 1, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
        private FestivalType festivalType;
        private Integer festivalCategory;
        private LocalDate festivalDate;
        private LocalTime festivalTime;


        // 공통으로 들어가는 속성 (필수 값)
        private Ticketing.TicketingBuilder buildCommonProperties() {
            return Ticketing.builder()
                    .title(title)
                    .content(content)
                    .view(0L);
        }

        // 축제,공연 연동 X
        public Ticketing toEntity(TemporaryUser tempUser) {
            return buildCommonProperties()
                    .build();
        }

        // 축제,공연 연동 O
        public Ticketing toEntity(TemporaryUser tempUser, Festival festival) {
            return buildCommonProperties()
                    .temporaryUser(tempUser)
                    .festival(festival)
                    .build();
        }
    }
}
