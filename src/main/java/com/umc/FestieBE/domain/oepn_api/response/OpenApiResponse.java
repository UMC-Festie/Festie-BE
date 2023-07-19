package com.umc.FestieBE.domain.oepn_api.response;


import com.umc.FestieBE.domain.oepn_api.dto.OpenApiDTO;

import java.util.List;

//openapi로부터 받아온 응답의 형태를 매핑하기 위해 사용된 클래스.
//dto는 애플리케이션에서 사용되는 데이터를 담는 클래스입니다.
public class OpenApiResponse {
    private boolean isSuccess;
    private int code;
    private String message;
    private OpenApiResponse.Result result;

    public static class Result {
        private int numberOfElements;
        private List<OpenApiDTO> dto;

        public List<OpenApiDTO> getDto(){

            return dto;
        }
    }

    //performances는 FestivalDTO 클래스의 리스트로 매핑됨.
    //FestivalDTO클래스는 OpenApi에서 받아온 데이터를 매핑하는데 사용되는 클래스.
    private List<OpenApiDTO> performances;

    public OpenApiResponse.Result getResult() {

        return result;
    }


}
