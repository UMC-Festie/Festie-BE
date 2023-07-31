package com.umc.FestieBE.global.type;

import com.umc.FestieBE.domain.festival.dto.FestivalPaginationResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DurationType {
    PERFORMANCE_UPCOMING ("공연예정"),
    PERFORMANCE_ONGOING ("공연중"),
    PERFORMANCE_FINISHED("공연종료"),
    FESTIVAL_UPCOMING("축제예정"),
    FESTIVAL_ONGOING("축제중"),
    FESTIVAL_FINISHED("축제종료");

    private final String duration;

    public static DurationType fromDday(FestivalType festivalType, String duration) {

        if (festivalType.getValue() == 0){ // [공연]
            if("공연예정".equals(duration)) {
                return PERFORMANCE_UPCOMING;
            } else if ("공연종료".equals(duration)) {
                return PERFORMANCE_FINISHED;
            } else {
                return PERFORMANCE_ONGOING;
            }
        }
        else { // [축제]
            if("축제예정".equals(duration)) {
                return FESTIVAL_UPCOMING;
            } else if ("축제종료".equals(duration)) {
                return FESTIVAL_FINISHED;
            } else {
                return FESTIVAL_ONGOING;
            }
        }
    }
}