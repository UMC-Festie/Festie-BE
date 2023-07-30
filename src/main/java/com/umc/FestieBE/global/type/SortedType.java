package com.umc.FestieBE.global.type;


import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SortedType {
    LATEST("최신순", 0),
    OLDEST("오래된순",1),
    MOST_VIEWED("조회많은순",2),
    LEAST_VIEWED("조회적은순",3),
    MOST_LIKED("좋아요많은순",4),
    LEAST_LIKED("좋아요적은순",5);

    private final String sorted;
    private final int value;
}