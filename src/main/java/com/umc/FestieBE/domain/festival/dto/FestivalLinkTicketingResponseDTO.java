package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;


@Getter
public class FestivalLinkTicketingResponseDTO {
    private String festivalId;
    private String boardType;
    private String thumbnailUrl;
    private String festivalTitle;
    private Boolean isDeleted;

    /** 티켓팅 */
    // 공연, 축제 연동 O
    public FestivalLinkTicketingResponseDTO(Festival festival){
        this.festivalId = String.valueOf(festival.getId());
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.festivalTitle = festival.getFestivalTitle();
        this.isDeleted = festival.getIsDeleted();
    }

    public FestivalLinkTicketingResponseDTO(OpenPerformance op, Boolean isDeleted){
        this.festivalId = op.getId();
        this.thumbnailUrl = op.getDetailUrl();
        this.festivalTitle = op.getFestivalTitle();
        this.isDeleted = isDeleted;
    }

    public FestivalLinkTicketingResponseDTO(OpenFestival of, Boolean isDeleted){
        this.festivalId = of.getId();
        this.thumbnailUrl = of.getDetailUrl();
        this.festivalTitle = of.getFestivalTitle();
        this.isDeleted = isDeleted;
    }

    // 공연, 축제 연동 X (직접 입력)
    public FestivalLinkTicketingResponseDTO(Ticketing ticketing){
        this.festivalId = null;
        this.thumbnailUrl = ticketing.getThumbnailUrl();
        this.festivalTitle = ticketing.getFestivalTitle();
    }
}
