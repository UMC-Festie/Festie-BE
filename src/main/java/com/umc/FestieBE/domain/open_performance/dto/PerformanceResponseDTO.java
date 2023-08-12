package com.umc.FestieBE.domain.open_performance.dto;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class PerformanceResponseDTO {

    @Getter
    //공연 정보보기
    public static class PerformanceResponse {
        //공연 목록
        private Long totalCount;
        private Integer pageNum;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private List<PerformanceDetailResponse> data;

        //Entity -> DTO
        public PerformanceResponse(List<PerformanceDetailResponse> data, Long totalCount, Integer pageNum, Boolean hasNext, Boolean hasPrevious) {
           this.totalCount = totalCount;
           this.pageNum = pageNum;
           this.hasNext = hasNext;
           this.hasPrevious = hasPrevious;
           this.data = data;

        }
    }

    @Getter
    public static class PerformanceDetailResponse{
        private String id;
        private String name;
        private String startDate;
        private String endDate;
        private String location;
        private String profile;
        private String genrenm;
        private String state;

        //Entity -> DTO
        public PerformanceDetailResponse(OpenPerformance openPerformance){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d");

            this.id = openPerformance.getId();
            this.name = openPerformance.getFestivalTitle();
            this.startDate = openPerformance.getStartDate().format(formatter);
            //this.startDate = openPerformance.getStartDate();
            this.endDate = openPerformance.getEndDate().format(formatter);
            //this.endDate = openPerformance.getEndDate();
            this.location = openPerformance.getLocation();
            this.profile = openPerformance.getDetailUrl();
            this.genrenm = openPerformance.getGenrenm();
            this.state = openPerformance.getState();
        }
    }


}
