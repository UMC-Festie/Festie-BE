package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;
import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TogetherResponseDTO {
    @Getter
    public static class TogetherDetailResponse {
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
        private Boolean isDeleted;
        private FestivalLinkResponseDTO festivalInfo;

        // Bestie
        private Boolean isWriter;
        private Boolean isApplicant;
        private Boolean isApplicationSuccess;
        private Integer status; // 매칭 상태
        private List<ApplicantInfoResponseDTO> applicantList;
        private String message;


        // Entity -> DTO
        public TogetherDetailResponse (Together together, List<ApplicantInfoResponseDTO> applicantList,
                                    Boolean isLinked, Boolean isDeleted, FestivalLinkResponseDTO festivalInfo,
                                    Boolean isWriter, Boolean isApplicant, Boolean isApplicationSuccess){
            // 작성 날짜: LocalDateTime -> '년도.월.일' 형식으로 변경
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String updatedDate = together.getUpdatedAt().format(formatter);

            this.togetherDate = together.getDate().format(formatter);
            this.togetherTime = String.valueOf(together.getTime());
            this.title = together.getTitle();
            this.writerNickname = together.getUser().getNickname(); //임시 유저
            this.updatedDate = updatedDate;
            this.applicantCount = applicantList.size();
            this.view = together.getView();
            this.content = together.getContent();
            this.target = together.getTarget();

            this.isLinked = isLinked;
            this.isDeleted = isDeleted;
            this.festivalInfo = festivalInfo;

            this.isWriter = isWriter;
            this.isApplicant = isApplicant;
            this.isApplicationSuccess = isApplicationSuccess;
            this.status = together.getStatus();
            this.applicantList = isWriter ? applicantList : null;
            this.message = (isWriter || isApplicationSuccess) ? together.getMessage() : null;
        }

    }


    @Getter
    public static class TogetherListResponse {
        private Long totalCount;
        private Integer pageNum;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private List<TogetherListDetailResponse> data;

        public TogetherListResponse(List<TogetherListDetailResponse> data,
                                    Long totalCount, Integer pageNum, Boolean hasNext, Boolean hasPrevious){
            this.totalCount = totalCount;
            this.pageNum = pageNum;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
            this.data = data;
        }
    }

    @Getter
    public static class TogetherListDetailResponse {
        private Long togetherId;
        private String thumbnailUrl;
        private String type;
        private String category;
        private String region;
        private Integer status; // 모집중:0, 모집완료:1
        private String title;
        private String nickname;
        private String festivalDate;

        public TogetherListDetailResponse(Together together){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.togetherId = together.getId();
            this.thumbnailUrl = together.getThumbnailUrl();
            this.type = together.getType().getType();
            this.category = together.getCategory().getCategory();
            this.region = together.getRegion().getRegion();
            this.status = together.getStatus();
            this.title = together.getTitle();
            this.nickname = together.getUser().getNickname();
            this.festivalDate = together.getDate().format(formatter);
        }
    }

    @Getter
    public static class TogetherHomeListResponse {
        private Long togetherId;
        private String thumbnailUrl;
        private Integer status;
        private String title;
        private String nickname;
        private String togetherDate;

        public TogetherHomeListResponse(Together together){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            this.togetherId = together.getId();
            this.thumbnailUrl = together.getThumbnailUrl();
            this.status = together.getStatus();
            this.title = together.getTitle();
            this.nickname = together.getUser().getNickname();
            this.togetherDate = together.getDate().format(formatter);
        }
    }

}
