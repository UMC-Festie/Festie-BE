package com.umc.FestieBE.domain.openapi_2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {
    private String id;
    private String name;
    private String profile;
    private String startDate;
    private String endDate;
    //요일 시간
    private String dateTime;
    //총 시간
    private String runtime;
    private String location;
    private String price;

//    private String information;
//    private String view;
    private String details;
    private String images;
    private String management;
    private String managementPhone;
//         "id": 20,
//         "name": "홍길동",
//         "profile": "1.jpg",
//         "startDate": "2023-05-30",
//         "endDate": "2023-07-22",
//         "time": "토요일 오후3시",
//         "location": "세종문화회관 체임버홀",
//         "information": "세종문화회관 체임버홀",
//         "view":15,
//         "datail": "2.jpg",
//         "image" : "3.jpg",
//         "management" : "(사)부산국제매직페스티벌 조직위원회",
//         "managementPhone" : 0516267002
}
