package com.umc.FestieBE.domain.festival.dto;

import lombok.Getter;

import java.util.List;

public class FestivalSearchResponseDTO {

    @Getter
    public static class FestivalListResponse {
        private List<FestivalListDetailResponse> data;
    }

    @Getter
    public static class FestivalListDetailResponse {
        private Long festivalId;
        private String boardType; // 정보보기/정보공유
        private String festivalType; // 공연/축제 유형
        private String festivalTitle; // 공연/축제 제목
    }
}
