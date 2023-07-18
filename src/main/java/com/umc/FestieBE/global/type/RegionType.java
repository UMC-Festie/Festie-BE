package com.umc.FestieBE.global.type;

import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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

    public static RegionType findRegionType(String region){
        return Arrays.stream(RegionType.values())
                .filter(r -> r.getRegion().equals(region))
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE, "해당하는 지역이 없습니다."));
    }

}
