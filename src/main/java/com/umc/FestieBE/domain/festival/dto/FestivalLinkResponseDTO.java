package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class FestivalLinkResponseDTO {

    private Long festivalId;
    private String thumbnailUrl;
    private String title;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;


    /** 같이가요 */
    // Entity -> DTO
    // 공연/축제 연동 O
    public FestivalLinkResponseDTO(Festival festival){
        this.festivalId = festival.getId();
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.title = festival.getFestivalTitle();
        this.region = festival.getRegion().getRegion();
        this.startDate = festival.getStartDate();
        this.endDate = festival.getEndDate();
    }

    // 공연/축제 연동 X (직접 입력)
    public FestivalLinkResponseDTO(Together together){
        this.festivalId = null;
        this.thumbnailUrl = together.getThumbnailUrl();
        this.title = together.getFestivalTitle();
        this.region = together.getRegion().getRegion();
        this.startDate = null;
        this.endDate = null;
    }
}
