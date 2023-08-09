package com.umc.FestieBE.global.type;


import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SortedType {
    LATEST("LATEST", "최신순"),
    OLDEST("OLDEST", "오래된순"),
    MOST_VIEWED("MOST_VIEWED", "조회높은순"),
    LEAST_VIEWED("LEAST_VIEWED", "조회낮은순"),
    MOST_LIKED("MOST_LIKED", "좋아요많은순"),
    LEAST_LIKED("LEAST_LIKED", "좋아요적은순");

    private final String sortBy;
    private final String description;

    public static SortedType findBySortBy(String sortBy) {
        for (SortedType type : values()) {
            if (type.sortBy.equalsIgnoreCase(sortBy)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
    }
}