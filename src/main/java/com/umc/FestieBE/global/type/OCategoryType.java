package com.umc.FestieBE.global.type;

import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OCategoryType {

    THEATER ("연극", 0),
    MUSICAL("뮤지컬", 1),
    CLASSIC("서양음악(클래식)", 2), // 서양음악(클래식)
    GUKAK("한국음악(국악)", 3), // 한국음악(국악)
    POPULAR_MUSIC("대중음악", 4),
    DANCE("무용", 5), // 무용(서양/한국무용)
    POPULAR_DANCE("대중무용", 6),
    CIRCUS_MAGIC("서커스/마술", 7), // 서커스/마술
    VARIETY("복합", 8);

    private final String category;
    private final int value;

    public static OCategoryType findCategoryType(Integer categoryType){
        return Arrays.stream(OCategoryType.values())
                .filter(c -> c.getValue() ==  categoryType)
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE, "해당하는 카테고리가 없습니다."));
    }

    public static OCategoryType findCategoryType(String category){
        return Arrays.stream(OCategoryType.values())
                .filter(c -> c.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE, "해당하는 카테고리가 없습니다.O"));
    }
}
