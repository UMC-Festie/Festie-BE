package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class FestivalLinkResponseDTO {

    private String festivalId;
    private String boardType;
    private String thumbnailUrl;
    private String title;
    private String region;
    //private LocalDate startDate;
    //private LocalDate endDate;


    /** 같이가요 */
    // Entity -> DTO
    // 공연/축제 연동 O
    public FestivalLinkResponseDTO(Festival festival){
        this.festivalId = String.valueOf(festival.getId());
        this.boardType = "정보공유";
        this.thumbnailUrl = festival.getThumbnailUrl();
        this.title = festival.getFestivalTitle();
        this.region = festival.getRegion().getRegion();
        //this.startDate = festival.getStartDate();
        //this.endDate = festival.getEndDate();
    }

    public FestivalLinkResponseDTO(OpenPerformance op){
        this.festivalId = op.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = null;
        //this.thumbnailUrl = op.getThumbnailUrl(); TODO op.getThumbnailUrl();
        this.title = op.getFestivalTitle();
        this.region = null;
        //this.region = op.getRegion().getRegion(); TODO op.getRegion();
        //this.startDate = festival.getStartDate();
        //this.endDate = festival.getEndDate();
    }

    public FestivalLinkResponseDTO(OpenFestival of){
        this.festivalId = of.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = null;
        //this.thumbnailUrl = op.getThumbnailUrl(); TODO op.getThumbnailUrl();
        this.title = of.getFestivalTitle();
        this.region = null;
        //this.region = op.getRegion().getRegion(); TODO op.getRegion();
        //this.startDate = festival.getStartDate();
        //this.endDate = festival.getEndDate();
    }

    // 공연/축제 연동 X (직접 입력)
    public FestivalLinkResponseDTO(Together together){
        this.festivalId = null;
        this.boardType = null;
        this.thumbnailUrl = together.getThumbnailUrl();
        this.title = together.getFestivalTitle();
        this.region = together.getRegion().getRegion();
        //this.startDate = null;
        //this.endDate = null;
    }
}
