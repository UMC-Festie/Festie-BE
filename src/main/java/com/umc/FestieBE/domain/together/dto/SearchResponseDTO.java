package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SearchResponseDTO {

    @Getter
    public static class SearchListResponse {
        private List<SearchListDetailResponse> data;

        public SearchListResponse(List<SearchListDetailResponse> searchList){
            this.data = searchList;
        }
    }

    @Getter
    public static class SearchListDetailResponse {
        private String thumbnailUrl;
        private String title;
        private String content;
        private String updatedAt;
        private Long view;
        private Long likeCount;

        // 정보보기 (축제)
        public SearchListDetailResponse(OpenFestival of, Long view, Long likeCount){
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.thumbnailUrl = of.getDetailUrl();
            this.title = of.getFestivalTitle();
            this.content = null; //TODO 내용
            this.updatedAt = null; //TODO 날짜
            this.view = view; //TODO 조회수
            this.likeCount = likeCount; //TODO 좋아요 개수
        }

        // 정보보기 (공연)
        public SearchListDetailResponse(OpenPerformance op, Long view, Long likeCount){
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.thumbnailUrl = op.getDetailUrl();
            this.title = op.getFestivalTitle();
            this.content = null; //TODO 내용
            this.updatedAt = null; //TODO 날짜
            this.view = view; //TODO 조회수
            this.likeCount = likeCount; //TODO 좋아요 개수
        }

        // 정보공유
        public SearchListDetailResponse(Festival f){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.thumbnailUrl = f.getThumbnailUrl();
            this.title = f.getTitle();
            this.content = f.getContent();
            this.updatedAt = f.getUpdatedAt().format(formatter);
            this.view = f.getView();
            this.likeCount = f.getLikes(); //TODO 좋아요 개수
        }

        // 후기
        public SearchListDetailResponse(Review r, Long likeCount){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.thumbnailUrl = r.getThumbnailUrl();
            this.title = r.getTitle();
            this.content = r.getContent();
            this.updatedAt = r.getUpdatedAt().format(formatter);
            this.view = r.getView();
            this.likeCount = likeCount; //TODO 좋아요 개수
        }

        // 티켓팅
        public SearchListDetailResponse(Ticketing t){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.thumbnailUrl = t.getThumbnailUrl();
            this.title = t.getTitle();
            this.content = t.getContent();
            this.updatedAt = t.getUpdatedAt().format(formatter);
            this.view = t.getView();
            this.likeCount = t.getLikes();
        }

        // 같이가요
        public SearchListDetailResponse(Together t){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.thumbnailUrl = t.getThumbnailUrl();
            this.title = t.getTitle();
            this.content = t.getContent();
            this.updatedAt = t.getUpdatedAt().format(formatter);
            this.view = t.getView();
            this.likeCount = null;
        }
    }
}
