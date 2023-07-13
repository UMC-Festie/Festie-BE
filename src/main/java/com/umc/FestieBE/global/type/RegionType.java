package com.umc.FestieBE.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegionType {

    SEOUL ("서울", 0),
    GYEONGGI("경기", 1),
    INCHEON("인천", 2),
    DAEJEON ("대전", 3),
    DAEGU ("대구", 4),
    GWANGJU ("광주", 5),
    BUSAN ("부산", 6),
    ULSAN ("울산", 7),
    SEJONG ("세종", 8),
    CHUNGCHEONG ("충청", 9),
    GYEONGSANG ("경상", 10),
    JEOLLA ("전라", 11),
    GANGWON ("강원", 12),
    JEJU ("제주", 13)
    ;

    private final String region;
    private final int value;

}
