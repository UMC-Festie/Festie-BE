package com.umc.FestieBE.domain.oepn_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestievalResponseDTO {
    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String location;
    private String profile;
    private String genrenm;
    private String state;

}
