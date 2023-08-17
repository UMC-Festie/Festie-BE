package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;
@Getter
public class FestivalLinkReviewResponseDTO {
    private Long festivalId;
    private String thumbnailUrl;
    private String festivalTitle;
    private Boolean isDeleted;

    // 공연, 축제 연동 O
    public FestivalLinkReviewResponseDTO(Festival festival){
        this.festivalId = festival.getId();
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.festivalTitle = festival.getFestivalTitle();
        this.isDeleted = festival.getIsDeleted();
    }

    // 공연, 축제 연동 X (직접 입력)
    public FestivalLinkReviewResponseDTO(Review review){
        this.festivalId = null;
        this.thumbnailUrl = review.getThumbnailUrl();
        this.festivalTitle = review.getFestivalTitle();
    }
}

