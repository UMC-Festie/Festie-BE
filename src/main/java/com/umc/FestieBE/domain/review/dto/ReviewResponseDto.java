package com.umc.FestieBE.domain.review.dto;

import com.umc.FestieBE.domain.festival.dto.FestivalLinkReviewResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkTicketingResponseDTO;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ReviewResponseDto {
    @Getter
    public static class ReviewDetailResponse {
        private String postTitle;
        private String content;
        private String writer;
        //private String startDate;
        //private String endDate;
        private String updatedDate;
        private Long view;
        private Long likes;
        private Long dislikes;
        private List<String> imagesUrl;

        //축제 정보
        private Boolean isWriter; //작성자 확인인지?
        private Boolean isLinked;
        private Integer isLikedOrDisliked;

        public ReviewDetailResponse(Review review, Boolean isWriter, Boolean isLinked, FestivalLinkReviewResponseDTO festivalLinkReviewResponseDTO, Integer isLikedOrDisliked) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String updatedDate = review.getUpdatedAt().format(dateFormatter);

            this.postTitle = review.getTitle();
            this.content = review.getContent();
            this.writer = review.getUser().getNickname();
            this.updatedDate = updatedDate;
            this.view = review.getView();

            this.isLinked = isLinked;
            this.isWriter = isWriter;
            this.isLikedOrDisliked = isLikedOrDisliked;

            this.likes = review.getLikes();
            this.dislikes = review.getDislikes();
            this.imagesUrl = review.getImagesUrl();
        }




    }
}
