package com.umc.FestieBE.domain.festival.dto;

import lombok.Builder;

@Builder
public class FestivalResponseDTO {
    private Long festivalId;
    private String thumbnailUrl;
    private String title;
    private String region;
    private String startDate;
    private String endDate;
}
