package com.umc.FestieBE.global.type;

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
                .orElseThrow(() -> new IllegalArgumentException("해당하는 공연/축제 유형이 없습니다."));
    }

}
