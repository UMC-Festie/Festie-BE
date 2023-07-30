package com.umc.FestieBE.domain.oepn_api.dto;

import lombok.Getter;

public class FestivalResponseDTO {

    @Getter
    public static class FestivalHomeListResponse{
        private String thumbnailUrl;
        private Integer status;
        private String title;
        private String location;
        private String startDate;
        private String endDate;
    }
}
