package com.umc.FestieBE.global.type;

import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FestivalType {

    PERFORMANCE("공연", 0),
    FESTIVAL("축제", 1);

    private final String type;
    private final int value;

    public static FestivalType findFestivalType(Integer festivalType){
        return Arrays.stream(FestivalType.values())
                .filter(f -> f.getValue() == festivalType)
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE, "해당하는 공연/축제 타입이 없습니다."));
    }

}
