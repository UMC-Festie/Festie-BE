package com.umc.FestieBE.domain.ticketing.dto;

import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkTicketingResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Data;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketingResponseDTO {
    // 티켓팅 상세조회
    @Getter
    public static class TicketingDetailResponse {
        /** 티켓팅 조회 시
         *  1. 꼭 필요한 정보 -> title, content, 작성자, 작성 날짜, 조회수, 좋아요, 싫어요
         *  2. 있어도 되고 없어도 되는 정보 -> festivalTitle, thumbnailUrl, festivalDate, festivalTime
         *      1) 만약 축제를 연동한 경우 : festivalTitle, thumbnailUrl은 연동된 내용으로 변경
         *      2) 연동 안한 경우 : festivalTitle, thumbnailUrl, festivalDate, festivalTime 다 사용자가 입력
         */

        private String title;
        private String content;
        private String writerNickname;
        private String updatedDate;
        private Long view;
        private Long likes;
        private Long dislikes;
        private List<String> imagesUrl;
        private String ticketingDate;
        private String ticketingTime;

        private Boolean isWriter; // 작성자 여부
        private Boolean isLinked; // 축제,공연 연동 여부

        private Integer isLikedOrDisliked; // 좋아요, 싫어요 여부
        // null: 안누름, 1: 좋아요 누름, 0: 싫어요 누름

        private FestivalLinkTicketingResponseDTO festivalInfo; // festivalTitle, thumbnailUrl

        public TicketingDetailResponse(Ticketing ticketing,
                                       Boolean isLinked,
                                       Boolean isWriter,
                                       FestivalLinkTicketingResponseDTO festivalInfo,
                                       Integer isLikedOrDisliked
                                       ) {
            // 날짜 형식 -> "년도.월.일' 형식으로 변경
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.dd");
            String updatedDate = ticketing.getUpdatedAt().format(dateFormatter);
            //String ticketingDate = ticketing.getTicketingDate().format(dateFormatter);
            String ticketingDate = ticketing.getTicketingDate() != null ? ticketing.getTicketingDate().format(dateFormatter) : null;

            // 시간 형식 -> "00:00 ~" 형식으로 변경
            DateTimeFormatter TimeFormatter = DateTimeFormatter.ofPattern("HH:mm ~");
            //String ticketingTime = ticketing.getTicketingTime().format(TimeFormatter);
            String ticketingTime = ticketing.getTicketingTime() != null ? ticketing.getTicketingTime().format(dateFormatter) : null;

            this.title = ticketing.getTitle();
            this.content = ticketing.getContent();
            this.writerNickname = ticketing.getUser().getNickname();
            this.updatedDate = updatedDate;
            this.view = ticketing.getView();

            this.isLinked = isLinked;
            this.isWriter = isWriter;
            this.isLikedOrDisliked = isLikedOrDisliked;

            this.festivalInfo = festivalInfo;
            this.ticketingDate = ticketingDate;
            this.ticketingTime = ticketingTime;
            this.likes = ticketing.getLikes();
            this.dislikes = ticketing.getDislikes();
            this.imagesUrl = ticketing.getImagesUrl();
        }
    }

    // 티켓팅 목록조회 페이지 정보 값
    @Getter
    public static class TicketingListResponse {
        private Long totalCount;
        private Integer pageNum;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private List<TicketingResponseDTO.TicketingPaginationResponse> data;

        public TicketingListResponse(List<TicketingResponseDTO.TicketingPaginationResponse> data,
                                    Long totalCount,
                                    Integer pageNum,
                                    Boolean hasNext,
                                    Boolean hasPrevious) {
            this.totalCount = totalCount;
            this.pageNum = pageNum;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
            this.data = data;
        }
    }

    // 티켓팅 목록조회 Data값
    @Getter
    public static class TicketingPaginationResponse {
        private String title;
        private String content;
        private String updateAt;
        private Long view;
        private Long likes;
        private Long dislikes;
        private String ticketingImageUrl; // 티켓팅 게시글에 업로드한 사진 중 1번재 사진

        public TicketingPaginationResponse(Ticketing ticketing) {
            DateTimeFormatter dateFromatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String updatedAt = ticketing.getUpdatedAt().format(dateFromatter);

            String ticketingImageUrl = null;
            if (ticketing.getImagesUrl().size() != 0) { // 티켓팅 게시글에 업로드한 이미지가 있는 경우
                ticketingImageUrl = ticketing.getImagesUrl().get(0); // 티켓팅 게시글에 업로드한 사진 중 1번재 사진
            }

            this.title = ticketing.getTitle();
            this.content = ticketing.getContent();
            this.updateAt = updatedAt;
            this.view = ticketing.getView();
            this.likes = ticketing.getLikes();
            this.dislikes = ticketing.getDislikes();
            this.ticketingImageUrl = ticketingImageUrl;
        }
    }
}
