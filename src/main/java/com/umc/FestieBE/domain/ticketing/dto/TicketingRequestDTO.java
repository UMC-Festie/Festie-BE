package com.umc.FestieBE.domain.ticketing.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TicketingRequestDTO {
    @NotBlank(message = "티켓팅 제목은 필수 입력 값입니다.")
    private String title;
    @NotBlank(message = "티켓팅 작성 내용은 필수 입력 값입니다.")
    private String content;

    private Long festivalId;
    private String festivalTitle;
    private String thumbnailUrl;

    @Min(value = 0, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
    @Max(value = 1, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
    private Integer festivalType;

    private Integer festivalCategory;
    private String festivalDate;


    // 1. 축제, 공연 연동 O (festivalId != null인 경우)
    public Ticketing toEntity(TemporaryUser tempUser,
                              Festival festival) {
        return Ticketing.builder()
                .temporaryUser(tempUser)
                .view(0L)
                .title(title)
                .content(content)
                .festivalId(festivalId)
                .festivalTitle(festival.getFestivalTitle())
                .festivalDate(LocalDate.parse(festivalDate))
                .type(festival.getType())
                .category(festival.getCategory())
                .thumbnailUrl(festival.getThumbnailUrl())
                .build();
    }

    // 2. 축제, 공연 연동 X (festivalId == null인 경우)
    public Ticketing toEntity(TemporaryUser tempUser, FestivalType festivalType) {
        return Ticketing.builder()
                .temporaryUser(tempUser)
                .view(0L)
                .title(title)
                .content(content)
                .festivalId(festivalId)
                .festivalTitle(festivalTitle)
                .festivalDate(LocalDate.parse(festivalDate))
                .type(festivalType)
                .category(festivalCategory)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
