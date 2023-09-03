package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class FestivalResponseDTO {

    // 공연, 축제 상세조회
    @Getter
    public static class FestivalDetailResponse {
        private String dDay;
        private String festivalTitle;
        private String postTitle;
        private String content;
        private String location;
        private String category;
        private String region;
        private String startDate;
        private String endDate;
        private String startTime;

        private String type;

        private String reservationLink;
        private String thumbnailUrl;
        private Long view;

        private String adminsName;
        private String adminsPhone;
        private String adminsSiteAddress;

        private Boolean isWriter;
        private Boolean isDeleted;
        private Integer isLikedOrDisliked; // 좋아요, 싫어요 여부
        // null: 안누름, 1: 좋아요 누름, 0: 싫어요 누름

        private Long like;
        private Long dislike;

        private List<String> imagesUrl;

        public FestivalDetailResponse (Festival festival, Boolean isWriter, String dDay, Integer isLikedOrDisliked){
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String startDate = festival.getStartDate().format(dateFormatter);
            String endDate = festival.getEndDate().format(dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm ~");
            String startTime = festival.getStartTime().format(timeFormatter);

            this.type = festival.getType().getType();
            this.like = festival.getLikes();
            this.dislike = festival.getDislikes();
            this.dDay = dDay;
            this.festivalTitle = festival.getFestivalTitle();
            this.postTitle = festival.getTitle();
            this.content = festival.getContent();
            this.reservationLink = festival.getReservationLink();
            this.location = festival.getLocation();
            this.region = festival.getRegion().getRegion();
            this.category = festival.getCategory().getCategory();
            this.startDate = startDate;
            this.endDate = endDate;
            this.startTime = startTime;
            this.thumbnailUrl = festival.getThumbnailUrl();
            this.view = festival.getView();
            this.adminsName = festival.getAdminsName();
            this.adminsPhone = festival.getAdminsPhone();
            this.adminsSiteAddress = festival.getAdminsSiteAddress();
            this.imagesUrl = festival.getImagesUrl();
            this.isDeleted = festival.getIsDeleted();
            this.isWriter = isWriter;
            this.isLikedOrDisliked = isLikedOrDisliked;
        }
    }

    // 공연, 축제 목록조회
    @Getter
    public static class FestivalPaginationResponse {
        private String dDay;
        private String festivalTitle;
        private String location;
        private String festivalDate; // 목록조회에서 표시될 공연 기간 (ex. 2023.5.30 - 2023.8.20)
        private String thumbnailUrl;
        private Boolean isDeleted;
        private Long likes;
        private Long dislikes;
        private String type;

        public FestivalPaginationResponse (Festival festival, String dDay) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String startDate = festival.getStartDate().format(dateFormatter);
            String endDate = festival.getEndDate().format(dateFormatter);
            String festivalDate = startDate + " - " + endDate;

            this.thumbnailUrl = festival.getThumbnailUrl();
            this.festivalTitle = festival.getFestivalTitle();
            this.location = festival.getLocation();
            this.festivalDate = festivalDate;
            this.isDeleted = festival.getIsDeleted();
            this.dDay = dDay;
            this.likes = festival.getLikes();
            this.dislikes = festival.getDislikes();
            this.type = festival.getType().getType();
        }
    }

    // 공연, 축제 무한스크롤
    @Getter
    public static class FestivalListResponse {
        private Long totalCount;
        private Integer pageNum;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private List<FestivalPaginationResponse> data;

        public FestivalListResponse(List<FestivalPaginationResponse> data,
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
}
