package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TogetherResponseDTO {
    // 같이가요
    private String togetherDate;
    private String togetherTime;
    private String title;
    private String writerNickname;
    private Long view;
    private String content;
    private String target;

    // 공연/축제 정보
    private Boolean isLinked;
    private FestivalResponseDTO festivalInfo;

    // Bestie
    private Boolean isWriter;
    private Boolean isApplicant;
    private Boolean isApplicationSuccess;
    private Integer status; // 매칭 상태
    private List<ApplicantInfoResponseDTO> applicantList;
    private String message;


    // Entity -> DTO
    public TogetherResponseDTO (Together together, List<ApplicantInfoResponseDTO> applicantList,
                                Boolean isLinked,FestivalResponseDTO festivalInfo,
                                Boolean isWriter, Boolean isApplicant, Boolean isApplicationSuccess){
        this.togetherDate = String.valueOf(together.getDate());
        this.togetherTime = String.valueOf(together.getTime());
        this.title = together.getTitle();
        this.writerNickname = together.getTemporaryUser().getNickname(); //임시 유저
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
