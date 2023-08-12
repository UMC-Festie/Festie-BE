package com.umc.FestieBE.domain.open_performance.dto;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import lombok.Getter;

import java.util.List;

public class PerformanceResponseDTO {

    @Getter
    //공연 정보보기
    public static class PerformanceListResponse {
        //공연 목록
//        private Long totalCount;
        private Integer pageNum;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private List<PerformanceDetailResponse> data;

        //Entity -> DTO
        public PerformanceListResponse(List<PerformanceDetailResponse> data, Integer pageNum, Boolean hasNext, Boolean hasPrevious) {
//           this.totalCount = totalCount;
           this.pageNum = pageNum;
           this.hasNext = hasNext;
           this.hasPrevious = hasPrevious;
           this.data = data;

        }
    }

    @Getter
    public static class PerformanceDetailResponse{
        private String performanceId;
        private String name;
        private String startDate;
        private String endDate;
        private String location;
        private String profile;
        private String category;
        private String duration;

        //Entity -> DTO
        public PerformanceDetailResponse(OpenPerformance openPerformance){
            this.performanceId = openPerformance.getId();
            this.name = openPerformance.getFestivalTitle();
            this.startDate = openPerformance.getStartDate();
            this.endDate = openPerformance.getEndDate();
            this.location = openPerformance.getLocation();
            this.profile = openPerformance.getDetailUrl();
            this.category = String.valueOf(openPerformance.getCategory());
            this.duration = String.valueOf(openPerformance.getDuration());
        }
    }


}
