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
    private String adminsSiteAddress;

    private Boolean isWriter;
    private Boolean isDeleted;

    private Long like;
    private Long dislike;

    private List<String> imagesUrl;


    public FestivalResponseDTO (Festival festival, Boolean isWriter, String dDay, Long like, Long dislike){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String startDate = festival.getStartDate().format(dateFormatter);
        String endDate = festival.getEndDate().format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm ~");
        String startTime = festival.getStartTime().format(timeFormatter);

        this.like = like;
        this.dislike = dislike;
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
        this.adminsSiteAddress = festival.getAdminsSiteAddress();
        this.isDeleted = festival.getIsDeleted();
        this.imagesUrl = festival.getImagesUrl();

        this.isWriter = isWriter;
    }
}
