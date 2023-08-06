package com.umc.FestieBE.domain.ticketing.dto;

import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class TicketingPaginationResponseDTO {
    private String title;
    private String content;
    private String updateAt;
    private Long view;
    private Long likes;
    private String ticketingImageUrl; // 티켓팅 게시글에 업로드한 사진 중 1번재 사진

    public TicketingPaginationResponseDTO(Ticketing ticketing) {
        DateTimeFormatter dateFromatter = DateTimeFormatter.ofPattern("yyyy.NN.dd");
        String updatedAt = ticketing.getUpdatedAt().format(dateFromatter);

        String ticketingImageUrl = null;
        if (ticketing.getImagesUrl().size() != 0) { // 티켓팅 게시글에 업로드한 이미지가 있는 경우
            ticketingImageUrl = ticketing.getImagesUrl().get(0); // 티켓팅 게시글에 업로드한 사진 중 1번재 사진
        }

        this.title = ticketing.getTitle();
        this.content = ticketing.getContent();
        this.updateAt = updatedAt;
        this.view = ticketing.getView();
        this.likes = ticketing.getLikes();
        this.ticketingImageUrl = ticketingImageUrl;
    }
}
