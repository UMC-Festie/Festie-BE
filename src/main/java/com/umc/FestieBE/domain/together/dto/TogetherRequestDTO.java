package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TogetherRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class TogetherRequest {
        // 축제 정보
        private Long festivalId;

        private String thumbnailUrl;

        @NotBlank(message = "공연/축제 제목은 필수 입력 값입니다.")
        private String festivalTitle;

        @NotNull(message = "공연/축제 유형은 필수 입력 값입니다.")
        @Min(value = 0, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
        @Max(value = 1, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
        private Integer festivalType;

        @NotNull(message = "공연/축제 카테고리는 필수 입력 값입니다.")
        @Min(value = 0, message = "공연/축제 카테고리는 0부터 8까지의 정수 값입니다.")
        @Max(value = 8, message = "공연/축제 카테고리는 0부터 8까지의 정수 값입니다.")
        private Integer category;

        @NotBlank(message = "공연/축제 지역은 필수 입력 값입니다.")
        private String region;

        // 같이가요 게시글 정보
        @NotBlank(message = "같이 갈 날짜는 필수 입력 값입니다.")
        private String togetherDate;
        @NotBlank(message = "같이 갈 공연/축제 시간은 필수 입력 값입니다.")
        private String togetherTime;

        private String title;
        private String content;
        private String target;

        @NotBlank(message = "매칭 메세지는 필수 입력값입니다.")
        private String message;

        // DTO -> Entity
        public Together toEntity(TemporaryUser tempUser,
                                 FestivalType festivalType,
                                 CategoryType categoryType,
                                 RegionType regionType){
            return Together.builder()
                    // 같이가요 게시글 정보
                    .temporaryUser(tempUser) //임시 유저
                    .status(0) // 매칭 대기 중
                    .view(0L)
                    .date(LocalDate.parse(togetherDate))
                    .time(LocalTime.parse(togetherTime))
                    .title(title)
                    .content(content)
                    .target(target)
                    .message(message)
                    // 공연/축제 정보
                    .festivalId(festivalId)
                    .thumbnailUrl(thumbnailUrl)
                    .festivalTitle(festivalTitle)
                    .type(festivalType)
                    .category(categoryType)
                    .region(regionType)
                    .build();
        }

    }


    @Getter
    @NoArgsConstructor
    public static class BestieApplicationRequest {
        @NotNull(message = "같이가요 게시글 식별자는 필수 입력값입니다.")
        private Long togetherId;

        private String introduction;


        // DTO -> Entity
        public ApplicantInfo toEntity(TemporaryUser tempUser, Together together){
            return ApplicantInfo.builder()
                    .temporaryUser(tempUser) //임시유저
                    .together(together)
                    .introduction(introduction)
                    .isSelected(false)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BestieChoiceRequest {
        @NotNull(message = "같이가요 게시글 식별자는 필수 입력값입니다.")
        private Long togetherId;

        @NotEmpty(message = "Bestie로 선택할 사람(들)의 식별자 리스트는 필수 입력값입니다.")
        private List<Long> bestieList;
    }

}
