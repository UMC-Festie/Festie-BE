package com.umc.FestieBE.domain.oepn_api.dto;


import java.util.List;

//데이터를 전달하기 위한 객체, openapi에서 받아온 데이터를 자바 객체로 매핑하기 위해 사용.
//즉, 여기서 정리한 데이터를 client에게 전달하는 것

public class OpenApiDTO {
    private List<EventApiDTO> dto;

    public List<EventApiDTO> getDto() {
        return dto;
    }

    public void setDto(List<EventApiDTO> dto) {
        this.dto = dto;
    }
}
