package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
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
        private String festivalId;
        private String boardType; // 정보보기/정보공유
        private String festivalType; // 공연/축제 유형
        private String festivalTitle; // 공연/축제 제목

        public FestivalListDetailResponse(Festival festival, String boardType){
            this.festivalId = String.valueOf(festival.getId());
            this.boardType = boardType;
            this.festivalType = festival.getType().getType();
            this.festivalTitle = festival.getFestivalTitle();
        }

        public FestivalListDetailResponse(OpenPerformance op, String boardType){
            this.festivalId = op.getId();
            this.boardType = boardType;
            this.festivalType = "공연";
            this.festivalTitle = op.getFestivalTitle();
        }

        public FestivalListDetailResponse(OpenFestival of, String boardType){
            this.festivalId = of.getId();
            this.boardType = boardType;
            this.festivalType = "축제";
            this.festivalTitle = of.getFestivalTitle();
        }
    }

    // 공연/축제 정보 연동 - 선택
    @Getter
    public static class FestivalInfoResponse {
        private String festivalId;
        private String boardType;
        private String thumbnailUrl;
        private String festivalTitle;
        private String festivalType;
        private String category;
        private String region;

        public FestivalInfoResponse(Festival festival){
            this.festivalId = String.valueOf(festival.getId());
            this.boardType = "정보공유";
            this.thumbnailUrl = festival.getThumbnailUrl();
            this.festivalTitle = festival.getFestivalTitle();
            this.festivalType = festival.getType().getType();
            this.category = festival.getCategory().getCategory();
            this.region = festival.getRegion().getRegion();
        }

        public FestivalInfoResponse(OpenPerformance op){
            this.festivalId = op.getId();
            this.boardType = "정보보기";
            this.thumbnailUrl = null; // TODO thumbnailUrl: op.getThumbnailUrl();
            this.festivalTitle = op.getFestivalTitle();
            this.festivalType = "공연";
            this.category = op.getGenrenm();
            this.region = null; // TODO region
        }

        public FestivalInfoResponse(OpenFestival of){
            this.festivalId = of.getId();
            this.boardType = "정보보기";
            this.thumbnailUrl = null; // TODO thumbnailUrl: of.getThumbnailUrl();
            this.festivalTitle = of.getFestivalTitle();
            this.festivalType = "축제";
            this.category = of.getGenrenm();
            this.region = null; // TODO region
        }
    }
}
