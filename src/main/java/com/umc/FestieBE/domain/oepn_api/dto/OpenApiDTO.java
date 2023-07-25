package com.umc.FestieBE.domain.oepn_api.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

//데이터를 전달하기 위한 객체, openapi에서 받아온 데이터를 자바 객체로 매핑하기 위해 사용.
//즉, 여기서 정리한 데이터를 client에게 전달하는 것

@Getter
@Setter
public class OpenApiDTO {

    private List<Performance> performances;

    public List<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(List<Performance> performances) {
        this.performances = performances;
    }
}
