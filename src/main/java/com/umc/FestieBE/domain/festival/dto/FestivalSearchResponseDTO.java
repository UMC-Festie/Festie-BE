package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import lombok.Getter;

import java.util.List;

public class FestivalSearchResponseDTO {

    @Getter
    public static class FestivalListResponse {
        private List<FestivalListDetailResponse> data;

        public FestivalListResponse(List<FestivalListDetailResponse> data){
            this.data = data;
        }
    }

    @Getter
    public static class FestivalListDetailResponse {
        private Long festivalId;
        private String boardType; // 정보보기/정보공유
        private String festivalType; // 공연/축제 유형
        private String festivalTitle; // 공연/축제 제목

        public FestivalListDetailResponse(Festival festival, String boardType){
            this.festivalId = festival.getId();
            this.boardType = boardType;
            this.festivalType = festival.getType().getType();
            this.festivalTitle = festival.getFestivalTitle();
        }
    }

    @Getter
    public static class FestivalInfoResponse {
        private String thumbnailUrl;
        private String festivalType;
        private String category;
        private String region;

        public FestivalInfoResponse(Festival festival){
            this.thumbnailUrl = festival.getThumbnailUrl();
            this.festivalType = festival.getType().getType();
            this.category = String.valueOf(festival.getCategory()); //임시 카테고리
            this.region = festival.getRegion().getRegion();
        }
    }
}
