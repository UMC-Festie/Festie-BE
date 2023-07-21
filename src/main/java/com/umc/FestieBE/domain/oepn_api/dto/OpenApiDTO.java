package com.umc.FestieBE.domain.oepn_api.dto;


import java.util.List;

//데이터를 전달하기 위한 객체, openapi에서 받아온 데이터를 자바 객체로 매핑하기 위해 사용.
//즉, 여기서 정리한 데이터를 client에게 전달하는 것

public class OpenApiDTO {

    private boolean inSuccess;
    private int code;
    private String message;
    private List<Result> result;


    public static class Result {
        private int numberOfElements;
        private List<Dto> dto;

        public List<Dto> getDto(){
            return dto;
        }
        public  void setDto(List<Dto> dto){
             this.dto = dto;
        }

        public static class Dto {
            private int id;
            private String name;
            private String profile;
            private String location;
            private String startDate;
            private String endDate;

        }

    }
    public List<Result> getResult(){
        return result;
    }
    public void setResult(List<Result> result){
        this.result = result;
    }

    //Entity로 보내주기??가 필요한가

}
