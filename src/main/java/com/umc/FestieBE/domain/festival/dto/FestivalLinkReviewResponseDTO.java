package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class FestivalLinkReviewResponseDTO {
    private String festivalId;
    private String boardType;
    private String thumbnailUrl;
    private String festivalTitle;
    private Boolean isDeleted;

    // 공연, 축제 연동 시 해당
    private String region;
    private String startDate;
    private String endDate;
    // 공연, 축제 직접 입력 시 해당
    private String date;
    private String time;

    // 공연, 축제 연동 O
    public FestivalLinkReviewResponseDTO(Festival festival,
                                         Review review){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        this.festivalId = String.valueOf(festival.getId());
        this.boardType = "정보공유";
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.festivalTitle = festival.getFestivalTitle();
        this.isDeleted = festival.getIsDeleted();

        this.region = festival.getRegion().getRegion();
        this.startDate = festival.getStartDate().format(dateFormatter);
        this.endDate = festival.getEndDate().format(dateFormatter);

        this.date = review.getDate().format(dateFormatter);
        this.time = (review.getTime() != null) ? String.valueOf(review.getTime()) : null;
    }

    public FestivalLinkReviewResponseDTO(OpenFestival of, Boolean isDeleted,
                                         Review review){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        this.festivalId = of.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = of.getDetailUrl();
        this.festivalTitle = of.getFestivalTitle();
        this.isDeleted = isDeleted;

        this.region = of.getRegion().getRegion();
        this.startDate = of.getStartDate().format(dateFormatter);
        this.endDate = of.getEndDate().format(dateFormatter);

        this.date = review.getDate().format(dateFormatter);
        this.time = (review.getTime() != null) ? String.valueOf(review.getTime()) : null;
    }

    public FestivalLinkReviewResponseDTO(OpenPerformance op, Boolean isDeleted,
                                         Review review){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        this.festivalId = op.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = op.getDetailUrl();
        this.festivalTitle = op.getFestivalTitle();
        this.isDeleted = isDeleted;

        this.region = op.getRegion().getRegion();
        this.startDate = op.getStartDate().format(dateFormatter);
        this.endDate = op.getEndDate().format(dateFormatter);

        this.date = review.getDate().format(dateFormatter);
        this.time = (review.getTime() != null) ? String.valueOf(review.getTime()) : null;
    }

    // 공연, 축제 연동 X (직접 입력)
    public FestivalLinkReviewResponseDTO(Review review){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        this.festivalId = null;
        this.boardType = null;
        this.thumbnailUrl = review.getThumbnailUrl();
        this.festivalTitle = review.getFestivalTitle();
        this.isDeleted = false;

        this.region = null;
        this.startDate = null;
        this.endDate = null;

        this.date = review.getDate().format(dateFormatter);
        this.time = (review.getTime() != null) ? String.valueOf(review.getTime()) : null;
    }
}

