package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;
@Getter
public class FestivalLinkReviewResponseDTO {
    private String festivalId;
    private String boardType;
    private String thumbnailUrl;
    private String festivalTitle;
    private Boolean isDeleted;

    // 공연, 축제 연동 O
    public FestivalLinkReviewResponseDTO(Festival festival){
        this.festivalId = String.valueOf(festival.getId());
        this.boardType = "정보공유";
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.festivalTitle = festival.getFestivalTitle();
        this.isDeleted = festival.getIsDeleted();
    }

    public FestivalLinkReviewResponseDTO(OpenFestival of, Boolean isDeleted){
        this.festivalId = of.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = of.getDetailUrl();
        this.festivalTitle = of.getFestivalTitle();
        this.isDeleted = isDeleted;
    }

    public FestivalLinkReviewResponseDTO(OpenPerformance op, Boolean isDeleted){
        this.festivalId = op.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = op.getDetailUrl();
        this.festivalTitle = op.getFestivalTitle();
        this.isDeleted = isDeleted;
    }

    // 공연, 축제 연동 X (직접 입력)
    public FestivalLinkReviewResponseDTO(Review review){
        this.festivalId = null;
        this.thumbnailUrl = review.getThumbnailUrl();
        this.festivalTitle = review.getFestivalTitle();
    }
}

