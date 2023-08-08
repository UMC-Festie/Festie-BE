package com.umc.FestieBE.domain.open_performance.dto;

import lombok.Getter;

@Getter
public class PerformanceResponseDTO {

    public static class OpenPerformanceResponse {
        //공연 목록
        private String id;
        private String name;
        private String startDate;
        private String endDate;
        private String location;
        private String profile;
        private String genrenm;
        private String state;


        //Entity -> DTO
        public OpenPerformanceResponse() {


        }
    }
}
