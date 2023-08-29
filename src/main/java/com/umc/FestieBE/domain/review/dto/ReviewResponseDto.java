package com.umc.FestieBE.domain.review.dto;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkReviewResponseDTO;
import com.umc.FestieBE.domain.review.domain.Review;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class ReviewResponseDto {
    @Getter
    public static class ReviewDetailResponse {
        // 게시글 정보
        private String postTitle;
        private String writer;
        private String updatedDate;
        private Long view;
        private String content;
        private Long likes;
        private Long dislikes;
        private List<String> imagesUrl;
        private Boolean isWriter; //작성자 확인인지?
        private Integer isLikedOrDisliked;

        // 축제 정보
        private Boolean isLinked;
        private FestivalLinkReviewResponseDTO festivalInfo;

        public ReviewDetailResponse(Review review, Boolean isWriter, Boolean isLinked,
                                    FestivalLinkReviewResponseDTO festivalLinkReviewResponseDTO, Integer isLikedOrDisliked) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String updatedDate = review.getUpdatedAt().format(dateFormatter);

            this.postTitle = review.getTitle();
            this.content = review.getContent();
            this.writer = review.getUser().getNickname();
            this.updatedDate = updatedDate;
            this.view = review.getView();

            this.isWriter = isWriter;
            this.isLikedOrDisliked = isLikedOrDisliked;

            this.isLinked = isLinked;
            this.festivalInfo = festivalLinkReviewResponseDTO;

            this.likes = review.getLikes();
            this.dislikes = review.getDislikes();
            this.imagesUrl = review.getImagesUrl();
        }
    }
    @Getter
    public static class ReviewListResponse {
        private Long totalPage;
        private Integer pageNum;
        private Boolean previousPage;
        private Boolean nextPage;
        private List<ReviewResponseDto.ReviewPageResponse> reviewPageResponse;

        public ReviewListResponse(List<ReviewResponseDto.ReviewPageResponse> reviewPageResponse, Long totalPage,
                                  Integer pageNum, Boolean previousPage, Boolean nextPage) {
            this.reviewPageResponse = reviewPageResponse;
            this.totalPage = totalPage;
            this.pageNum = pageNum;
            this.previousPage = previousPage;
            this.nextPage = nextPage;
        }
    }
    @Getter
    public static class ReviewPageResponse {
        private String title;
        private String content;
        private String updatedAt;
        private Long view;
        private Long like;
        private Long dislike;
        private String reviewImage;

        public ReviewPageResponse(Review review){
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String updatedAt = review.getUpdatedAt().format(dateFormatter);

            String reviewImage = null;
            if (review.getImagesUrl().size() != 0)  // 사용자가 게시글에 이미지를 첨부했을 때
                reviewImage = review.getImagesUrl().get(0); // 해당 이미지들의 제일 첫번째 사진을 띄어준다

            this.title = review.getTitle();
            this.content = review.getContent();
            this.updatedAt = updatedAt;
            this.view = review.getView();
            this.like = review.getLikes();
            this.reviewImage = reviewImage;
        }
    }
}
