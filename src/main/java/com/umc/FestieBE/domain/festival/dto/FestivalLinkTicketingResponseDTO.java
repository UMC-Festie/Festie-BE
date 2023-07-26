package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;


@Getter
public class FestivalLinkTicketingResponseDTO {
    private Long festivalId;
    private String thumbnailUrl;
    private String festivalTitle;
    private Boolean isDeleted;

    /** 티켓팅 */
    // 공연, 축제 연동 O
    public FestivalLinkTicketingResponseDTO(Festival festival){
        this.festivalId = festival.getId();
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.festivalTitle = festival.getFestivalTitle();
        this.isDeleted = festival.getIsDeleted();
    }

    // 공연, 축제 연동 X (직접 입력)
    public FestivalLinkTicketingResponseDTO(Ticketing ticketing){
        this.festivalId = null;
        this.thumbnailUrl = ticketing.getThumbnailUrl();
        this.festivalTitle = ticketing.getFestivalTitle();
    }
}
