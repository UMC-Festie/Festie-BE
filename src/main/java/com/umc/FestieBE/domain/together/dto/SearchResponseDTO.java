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
        private List<SearchListDetailResponse> searchList;

        public SearchListResponse(List<SearchListDetailResponse> searchList){
            this.searchList = searchList;
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

            thumbnailUrl = of.getDetailUrl();
            title = of.getFestivalTitle();
            content = null; //TODO 내용
            updatedAt = null; //TODO 날짜
            view = view; //TODO 조회수
            likeCount = likeCount; //TODO 좋아요 개수
        }

        // 정보보기 (공연)
        public SearchListDetailResponse(OpenPerformance op, Long view, Long likeCount){
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            thumbnailUrl = op.getDetailUrl();
            title = op.getFestivalTitle();
            content = null; //TODO 내용
            updatedAt = null; //TODO 날짜
            view = view; //TODO 조회수
            likeCount = likeCount; //TODO 좋아요 개수
        }

        // 정보공유
        public SearchListDetailResponse(Festival f, Long likeCount){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            thumbnailUrl = f.getThumbnailUrl();
            title = f.getTitle();
            content = f.getContent();
            updatedAt = f.getUpdatedAt().format(formatter);
            view = f.getView();
            likeCount = likeCount; //TODO 좋아요 개수
        }

        // 후기
        public SearchListDetailResponse(Review r, Long likeCount){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            thumbnailUrl = r.getThumbnailUrl();
            title = r.getTitle();
            content = r.getContent();
            updatedAt = r.getUpdatedAt().format(formatter);
            view = r.getView();
            likeCount = likeCount; //TODO 좋아요 개수
        }

        // 티켓팅
        public SearchListDetailResponse(Ticketing t, Long likeCount){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            thumbnailUrl = t.getThumbnailUrl();
            title = t.getTitle();
            content = t.getContent();
            updatedAt = t.getUpdatedAt().format(formatter);
            view = t.getView();
            likeCount = likeCount; //TODO 좋아요 개수
        }

        // 같이가요
        public SearchListDetailResponse(Together t){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            thumbnailUrl = t.getThumbnailUrl();
            title = t.getTitle();
            content = t.getContent();
            updatedAt = t.getUpdatedAt().format(formatter);
            view = t.getView();
            likeCount = null;
        }
    }
}
