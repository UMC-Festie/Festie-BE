package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class FestivalPaginationResponseDTO {
    private String dDay;
    private String festivalTitle;
    private String location;

    private Integer TotalCount; // 검색결과 건수

    private String festivalDate; // 무한스크롤에서 제공할 공연 기간 (ex. 2023.5.30 - 2023.8.20)
    private String thumbnailUrl;

    // TODO 삭제 처리된건 무한스크롤에 반영되면 안됨
    private Boolean isDeleted;

    public FestivalPaginationResponseDTO(Festival festival, String dDay, Integer totalCount) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.dd");
        String startDate = festival.getStartDate().format(dateFormatter);
        String endDate = festival.getEndDate().format(dateFormatter);
        String festivalDate = startDate + " - " + endDate;

        this.thumbnailUrl = festival.getThumbnailUrl();
        this.festivalTitle = festival.getFestivalTitle();
        this.location = festival.getLocation();
        this.festivalDate = festivalDate;
        this.isDeleted = festival.getIsDeleted();
        this.dDay = dDay;
        this.TotalCount = totalCount;
    }
}
