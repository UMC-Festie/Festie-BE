package com.umc.FestieBE.domain.oepn_api.dto;


import lombok.Getter;
import lombok.Setter;

//데이터를 전달하기 위한 객체, openapi에서 받아온 데이터를 자바 객체로 매핑하기 위해 사용.
//즉, 여기서 정리한 데이터를 client에게 전달하는 것

@Getter
@Setter
public class PerformResponseDTO {
    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private String location;
    private String profile;
    private String genrenm;
    private String state;
    private Integer totalcount;

}
