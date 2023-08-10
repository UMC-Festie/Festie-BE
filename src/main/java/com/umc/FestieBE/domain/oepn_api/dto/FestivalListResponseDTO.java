package com.umc.FestieBE.domain.oepn_api.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

public class FestivalListResponseDTO {

    @Getter
    public static class FestivalHomeListResponse{
        //private Long festivalId;
        private String festivalId;
        private String thumbnailUrl;
        private Integer status;
        private Long dDay;
        private String title;
        private String location;
        private String startDate;
        private String endDate;

        /*
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
        */

        public FestivalHomeListResponse(OpenPerformance op, Integer status, Long dDay){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d");

            this.festivalId = op.getId();
            //this.thumbnailUrl = op.getThumbnailUrl();
            this.thumbnailUrl = null; //TODO op.getThumbnailUrl();
            this.status = status;
            this.dDay = dDay;
            this.title = op.getFestivalTitle();
            this.location = op.getLocation();
            this.startDate = op.getStartDate().format(formatter);
            this.endDate = op.getEndDate().format(formatter);
        }

        public FestivalHomeListResponse(OpenFestival of, Integer status, Long dDay){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d");

            this.festivalId = of.getId();
            //this.thumbnailUrl = op.getThumbnailUrl();
            this.thumbnailUrl = null; //TODO of.getThumbnailUrl();
            this.status = status;
            this.dDay = dDay;
            this.title = of.getFestivalTitle();
            this.location = of.getLocation();
            this.startDate = of.getStartDate().format(formatter);
            this.endDate = of.getEndDate().format(formatter);
        }
    }
}
