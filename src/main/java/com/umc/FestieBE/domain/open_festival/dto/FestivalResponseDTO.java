package com.umc.FestieBE.domain.open_festival.dto;

import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FestivalResponseDTO {

    //축제 정보보기
    @Getter
    public static class FestivalListResponse {
        private Long totalCount;
        private Integer pageNum;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private List<FestivalDetailResponse> data;

        public FestivalListResponse(List<FestivalDetailResponse> data, Long totalCount, Integer pageNum, Boolean hasNext, Boolean hasPrevious){
            this.totalCount = totalCount;
            this.pageNum = pageNum;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
            this.data = data;

        }
    }

    @Getter
    public static class FestivalDetailResponse{
        private String festivalId;
        private String name;
        private String startDate;
        private String endDate;
        private String location;
        private String profile;
        private String category;
        private String duration;

        //Entity -> DTO
        public FestivalDetailResponse(OpenFestival openFestival){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.festivalId = openFestival.getId();
            this.name = openFestival.getFestivalTitle();
            this.startDate = openFestival.getStartDate().format(formatter);
            this.endDate = openFestival.getEndDate().format(formatter);
            this.location = openFestival.getLocation();
            this.profile = openFestival.getDetailUrl();
            this.category = String.valueOf(openFestival.getCategory());
            this.duration = String.valueOf(openFestival.getDuration());
        }
    }

    @Getter
    @Setter
    public static class DetailResponseDTO{
        private String id;
        private String name ="";
        private String profile ="";
        private String startDate ="";
        private String endDate ="";
        //요일 시간
        private String dateTime ="";
        //총 시간
        private String runtime ="";
        private String location ="";
        private String price ="";
        private String details ="";
        private String images ="";
        private String management ="";
        private Long isWriter;
        private Long likes;
        private Long dislikes;
        private Long view;
    }


}
