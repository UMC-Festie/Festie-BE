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
        this.thumbnailUrl = op.getDetailUrl();
        this.title = op.getFestivalTitle();
        this.region = null;
        //this.region = op.getRegion().getRegion(); TODO op.getRegion();
        //this.startDate = festival.getStartDate();
        //this.endDate = festival.getEndDate();
    }

    public FestivalLinkResponseDTO(OpenFestival of){
        this.festivalId = of.getId();
        this.boardType = "정보보기";
        this.thumbnailUrl = of.getDetailUrl();
        this.title = of.getFestivalTitle();
        this.region = null;
        //this.region = op.getRegion().getRegion(); TODO op.getRegion();
        //this.startDate = festival.getStartDate();
        //this.endDate = festival.getEndDate();
    }

    // 정보보기 공연/축제 연동했지만 삭제된 경우
    public FestivalLinkResponseDTO(String festivalId, String boardType, String thumbnailUrl,
                                   String title, String region){
        this.festivalId = festivalId;
        this.boardType = boardType;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.region = region;
    }

    // 정보보기 정보 연동했지만 이후 삭제된 경우 + 공연/축제 연동 X (직접 입력)
    public FestivalLinkResponseDTO(Together together){
        this.festivalId = together.getFestivalId(); // 직접 입력했을 경우 null
        this.boardType = together.getBoardType(); // 직접 입력했을 경우 null
        this.thumbnailUrl = together.getThumbnailUrl();
        this.title = together.getFestivalTitle();
        this.region = together.getRegion().getRegion();
    }
}
