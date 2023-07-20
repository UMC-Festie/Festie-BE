package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;
import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class TogetherResponseDTO {
    // 같이가요
    private String togetherDate;
    private String togetherTime;
    private String title;
    private String writerNickname;
    private String updatedDate;
    private Integer applicantCount;
    private Long view;
    private String content;
    private String target;

    // 공연/축제 정보
    private Boolean isLinked;
    private FestivalLinkResponseDTO festivalInfo;

    // Bestie
    private Boolean isWriter;
    private Boolean isApplicant;
    private Boolean isApplicationSuccess;
    private Integer status; // 매칭 상태
    private List<ApplicantInfoResponseDTO> applicantList;
    private String message;


    // Entity -> DTO
    public TogetherResponseDTO (Together together, List<ApplicantInfoResponseDTO> applicantList,
                                Boolean isLinked, FestivalLinkResponseDTO festivalInfo,
                                Boolean isWriter, Boolean isApplicant, Boolean isApplicationSuccess){
        // 작성 날짜: LocalDateTime -> '년도.월.일' 형식으로 변경
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d");
        String updatedDate = together.getUpdatedAt().format(formatter);

        this.togetherDate = String.valueOf(together.getDate());
        this.togetherTime = String.valueOf(together.getTime());
        this.title = together.getTitle();
        this.writerNickname = together.getTemporaryUser().getNickname(); //임시 유저
        this.updatedDate = updatedDate;
        this.applicantCount = applicantList.size();
        this.view = together.getView();
        this.content = together.getContent();
        this.target = together.getTarget();

        this.isLinked = isLinked;
        this.festivalInfo = festivalInfo;

        this.isWriter = isWriter;
        this.isApplicant = isApplicant;
        this.isApplicationSuccess = isApplicationSuccess;
        this.status = together.getStatus();
        this.applicantList = applicantList;
        this.message = together.getMessage();
    }


}
