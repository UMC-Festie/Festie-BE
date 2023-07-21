package com.umc.FestieBE.domain.oepn_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventApiDTO {

        @JsonProperty("mt20id")
        private String mt20id;

        @JsonProperty("prfnm")
        private String prfnm;

        @JsonProperty("prfpdfrom")
        private String prfpdfrom;

        @JsonProperty("prfpdto")
        private String prfpdto;

        @JsonProperty("fcltynm")
        private String fcltynm;

        @JsonProperty("poster")
        private String poster;

        @JsonProperty("genrenm")
        private String genrenm;

        @JsonProperty("prfstate")
        private String prfstate;

        @JsonProperty("openrun")
        private String openrun;

        // Getter와 Setter 메서드 (생략)

}

