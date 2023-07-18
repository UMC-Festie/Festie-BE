package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TogetherRequestDTO {
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
    public Together toEntity(TemporaryUser tempUser, FestivalType festivalType, RegionType region){
        return buildCommonProperties()
                .temporaryUser(tempUser)
                .thumbnailUrl(thumbnailUrl)
                .festivalTitle(festivalTitle)
                .type(festivalType)
                .region(region)
                // 카테고리
                .build();
    }

    public Together toEntity(TemporaryUser tempUser, Festival festival){
        return buildCommonProperties()
                .temporaryUser(tempUser)
                .festival(festival)
                .build();
    }

    private Together.TogetherBuilder buildCommonProperties(){
        return Together.builder()
                .status(0) // 매칭 대기 중
                .view(0L)
                .date(LocalDate.parse(togetherDate))
                .time(LocalTime.parse(togetherTime))
                .title(title)
                .content(content)
                .target(target)
                .message(message);
    }
}
