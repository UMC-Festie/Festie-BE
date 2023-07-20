package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;


@Getter
public class FestivalLinkResponseDTO {

    private Long festivalId;
    private String thumbnailUrl;
    private String title;
    private String region;
    private String startDate;
    private String endDate;

    // Entity -> DTO
    public FestivalLinkResponseDTO(Together together, String startDate, String endDate){
        this.festivalId = together.getFestivalId();
        this.thumbnailUrl = together.getThumbnailUrl();
        this.title = together.getTitle();
        this.region = together.getRegion().getRegion();
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
