package com.umc.FestieBE.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FestivalType {

    PERFORMANCE("공연", 0),
    FESTIVAL("축제", 1);

    private final String type;
    private final int value;

}
