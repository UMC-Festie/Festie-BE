package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class FestivalResponseDTO {
    // TODO 디데이 쿼리 추가
    private String dDay;
    private String festivalTitle;
    private String postTitle;
    private String content;
    private String location;
    private RegionType region;
    private String startDate;
    private String endDate;
    private String startTime;

    private String reservationLink;

    private String thumbnailUrl;
    private Long view;

    private String adminsName;
    private String adminsPhone;
    private String adminsStieAddress;

    private Boolean isWritrer;
    private Boolean isDeleted;


    public FestivalResponseDTO (Festival festival, Boolean isWriter, String dDay){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String startDate = festival.getStartDate().format(dateFormatter);
        String endDate = festival.getEndDate().format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm ~");
        String startTime = festival.getStartTime().format(timeFormatter);

        // TODO 디데이 추가
        this.dDay = dDay;
        this.festivalTitle = festival.getFestivalTitle();
        this.postTitle = festival.getTitle();
        this.content = festival.getContent();
        this.reservationLink = festival.getReservationLink();
        this.location = festival.getLocation();
        this.region = festival.getRegion();
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.view = festival.getView();
        this.adminsName = festival.getAdminsName();
        this.adminsPhone = festival.getAdminsPhone();
        this.adminsStieAddress = festival.getAdminsSiteAddress();
        this.isDeleted = festival.getIsDeleted();

        this.isWritrer = isWriter;
    }
}
