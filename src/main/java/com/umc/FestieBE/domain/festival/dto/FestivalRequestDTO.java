package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

// 새로운 공연, 축제 (api 연동 X)
public class FestivalRequestDTO {
    @Getter
    public static class FestivalRequest {
        @NotBlank(message = "공연/축제 제목은 필수 입력 값입니다.")
        private String festivalTitle;

        @NotNull (message = "공연/축제 유형은 필수 입력 값입니다.")
        @Min(value = 0, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
        @Max(value = 1, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
        private Integer festivalType; // 공연 or 축제

        @NotBlank(message = "공연/축제 썸네일 이미지 url은 필수 입력 값입니다.")
        private String thumbnailUrl;

        @NotNull(message = "공연/축제 카테고리는 필수 입력 값입니다.")
        private Integer category;

        @NotBlank(message = "공연/축제 지역은 필수 입력 값입니다.")
        private Integer festivalRegion; // 공연,축제 지역

        @NotBlank(message = "공연/축제 상세 위치는 필수 입력 값입니다.")
        private String festivalLocation; // 공연,축제 상세 위치

        @NotBlank(message = "공연/축제 시작일은 필수 입력 값입니다.")
        private String startDate;
        
        @NotBlank(message = "공연/축제 종료일은 필수 입력 값입니다.")
        private String endDate;
        
        private String postTitle;
        private String content;

        private Festival.FestivalBuilder buildCommonProperties() {
            return Festival.builder()
                    .view(0L)
                    .festivalTitle(festivalTitle)
                    // .type(festivalType) -> 공연/축제 선택
                    .thumbnailUrl(thumbnailUrl)
                    // .category(category) -> 카테고리
                    // .region(festivalRegion) -> 지역
                    .location(festivalLocation)
                    .startDate(LocalDate.parse(startDate))
                    .endDate(LocalDate.parse(endDate));
        }

        public Festival toEntity(TemporaryUser tempUser) {
            return buildCommonProperties()
                    .temporaryUser(tempUser)
                    .build();
        }
    }
}
