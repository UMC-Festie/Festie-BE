package com.umc.FestieBE.domain.ticketing.dto;

import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkTicketingResponseDTO;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class TicketingResponseDTO {
    /** 티켓팅 조회 시
     *  1. 꼭 필요한 정보 -> title, content, 작성자, 작성 날짜, 조회수, 좋아요, 싫어요
     *  2. 있어도 되고 없어도 되는 정보 -> festivalTitle, thumbnailUrl, festivalDate, festivalTime
     *      1) 만약 축제를 연동한 경우 : festivalTitle, thumbnailUrl은 연동된 내용으로 변경
     *      2) 연동 안한 경우 : festivalTitle, thumbnailUrl, festivalDate, festivalTime 다 사용자가 입력
     */

    private String title;
    private String content;
    private String writerNickname;
    private String updatedDate;
    private Long view;

    // 좋아요, 싫어요 추가

    private Boolean isWriter;

    private Boolean isLinked; // 축제,공연 연동 여부
    private FestivalLinkTicketingResponseDTO festivalInfo; // festivalTitle, thumbnailUrl

    private String festivalDate;
    private String festivalTime;

    public TicketingResponseDTO(Ticketing ticketing,
                                Boolean isLinked, Boolean isWriter,
                                FestivalLinkTicketingResponseDTO festivalInfo) {
        // 날짜 형식 -> "년도.월.일' 형식으로 변경
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.dd");
        String updatedDate = ticketing.getUpdatedAt().format(dateFormatter);
        String festivalDate = ticketing.getFestivalDate().format(dateFormatter);

        // 시간 형식 -> "00:00 ~" 형식으로 변경
        DateTimeFormatter TimeFormatter = DateTimeFormatter.ofPattern("HH:mm ~");
        String festivalTime = ticketing.getFestivalTime().format(TimeFormatter);

        this.title = ticketing.getTitle();
        this.content = ticketing.getContent();
        this.writerNickname = ticketing.getTemporaryUser().getNickname();
        this.updatedDate = updatedDate;
        this.view = ticketing.getView();

        this.isLinked = isLinked;
        this.isWriter = isWriter;
        this.festivalInfo = festivalInfo;

        this.festivalDate = festivalDate;
        this.festivalTime = festivalTime;
    }
}
