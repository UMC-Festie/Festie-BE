package com.umc.FestieBE.domain.oepn_api.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

public class FestivalListResponseDTO {

    @Getter
    public static class FestivalHomeListResponse{
        private Long festivalId;
        private String thumbnailUrl;
        private Integer status;
        private Long dDay;
        private String title;
        private String location;
        private String startDate;
        private String endDate;

        public FestivalHomeListResponse(Festival festival, Integer status, Long dDay){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d");

            this.festivalId = festival.getId();
            this.thumbnailUrl = festival.getThumbnailUrl();
            this.status = status;
            this.dDay = dDay;
            this.title = festival.getFestivalTitle();
            this.location = festival.getLocation();
            this.startDate = festival.getStartDate().format(formatter);
            this.endDate = festival.getEndDate().format(formatter);
        }
    }
}
