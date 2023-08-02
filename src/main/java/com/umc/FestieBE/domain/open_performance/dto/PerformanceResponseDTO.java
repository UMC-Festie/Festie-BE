package com.umc.FestieBE.domain.open_performance.dto;

import lombok.Getter;

@Getter
public class PerformanceResponseDTO {

    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String location;
    private String profile;
    private String genrenm;
    private String state;

    //Entity -> DTO

}
