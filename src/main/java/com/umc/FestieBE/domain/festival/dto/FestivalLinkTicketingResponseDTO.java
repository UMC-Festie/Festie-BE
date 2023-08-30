package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;



public class FestivalLinkTicketingResponseDTO {
    @Getter
    public static class OpenFestivalLinkTicketingResponse {
        private String festivalId;
        private String boardType;
        private String thumbnailUrl;
        private String festivalTitle;
        private Boolean isDeleted;

        public OpenFestivalLinkTicketingResponse(OpenPerformance op, Boolean isDeleted){
            this.festivalId = op.getId();
            this.boardType = "정보보기";
            this.thumbnailUrl = op.getDetailUrl();
            this.festivalTitle = op.getFestivalTitle();
            this.isDeleted = isDeleted;
        }

        public OpenFestivalLinkTicketingResponse(OpenFestival of, Boolean isDeleted){
            this.festivalId = of.getId();
            this.boardType = "정보보기";
            this.thumbnailUrl = of.getDetailUrl();
            this.festivalTitle = of.getFestivalTitle();
            this.isDeleted = isDeleted;
        }
    }

    @Getter
    public static class FestivalLinkTicketingResponse {
        private String festivalId;
        private String boardType;
        private String festivalTitle;
        private Boolean isDeleted;

        /** 티켓팅 */
        // 공연, 축제 연동 O
        public FestivalLinkTicketingResponse(Festival festival){
            this.festivalId = String.valueOf(festival.getId());
            this.boardType = "정보공유";
            this.festivalTitle = festival.getFestivalTitle();
            this.isDeleted = festival.getIsDeleted();
        }

        // 공연, 축제 연동 X (직접 입력)
        public FestivalLinkTicketingResponse(Ticketing ticketing){
            this.festivalId = null;
            this.festivalTitle = ticketing.getFestivalTitle();
        }
    }
}
