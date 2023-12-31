package com.umc.FestieBE.domain.ticketing.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class TicketingRequestDTO {
    @NotBlank(message = "티켓팅 제목은 필수 입력 값입니다.")
    private String title;
    @NotBlank(message = "티켓팅 작성 내용은 필수 입력 값입니다.")
    private String content;

    private String festivalId; // 연동할 공연/축제 식별자
    private String boardType; // 연동할 공연/축제 게시글 유형(정보보기/정보공유)
    private String festivalTitle;
    private String thumbnailUrl;

    // @Min(value = 0, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
    // @Max(value = 1, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
    // private Integer festivalType;

    private LocalDate ticketingDate;
    private LocalTime ticketingTime;
    private List<String> imagesUrl;


    /** 1. 축제, 공연 연동 O */
    // 연동되는 데이터: thumbnail, festivalTitle
    public Ticketing toEntity(User user,
                              //Festival festival,
                              FestivalType festivalType,
                              String festivalTitle,
                              String festivalThumbnail,
                              List<String> imagesUrl) {
        return Ticketing.builder()
                .user(user)
                .view(0L)
                .likes(0L)
                .dislikes(0L)
                .title(title)
                .content(content)
                .festivalId(festivalId)
                .boardType(boardType)
                //.festivalTitle(festival.getFestivalTitle()) // 연동
                //.thumbnailUrl(festival.getThumbnailUrl()) // 연동
                .festivalTitle(festivalTitle) // 연동
                .type(festivalType)
                .thumbnailUrl(festivalThumbnail) // 연동
                .ticketingDate(ticketingDate)
                .ticketingTime(ticketingTime)
                .imagesUrl(imagesUrl)
                .build();
    }

    /** 2. 축제, 공연 연동 X */
    public Ticketing toEntity(User user, String thumbnailUrl, List<String> imagesUrl) {
        return Ticketing.builder()
                .user(user)
                .view(0L)
                .likes(0L)
                .dislikes(0L)
                .title(title)
                .content(content)
                .festivalId(festivalId)
                .festivalTitle(festivalTitle)
                .thumbnailUrl(thumbnailUrl)
                .ticketingDate(ticketingDate)
                .ticketingTime(ticketingTime)
                .imagesUrl(imagesUrl)
                .build();
    }
}
