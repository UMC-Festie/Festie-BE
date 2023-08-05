package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.global.type.SortedType;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class FestivalPaginationResponseDTO {
    private String dDay;
    private String festivalTitle;
    private String location;

    private Long TotalCount; // 검색결과 건수

    private String festivalDate; // 목록조회에서 표시될 공연 기간 (ex. 2023.5.30 - 2023.8.20)
    private String thumbnailUrl;

    private Boolean isDeleted;


    public FestivalPaginationResponseDTO(Festival festival, String dDay, Long totalCount) {
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
