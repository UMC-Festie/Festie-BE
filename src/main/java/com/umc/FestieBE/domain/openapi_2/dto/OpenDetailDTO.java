package com.umc.FestieBE.domain.openapi_2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenDetailDTO {
    public class OpenApiDetailDTO {
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

        @JsonProperty("prfcast")
        private String prfcast;

        @JsonProperty("prfcrew")
        private String prfcrew;

        @JsonProperty("dtguidance")
        private String dtguidance;

        @JsonProperty("prfruntime")
        private String prfruntime;

        @JsonProperty("prfage")
        private String prfage;

        @JsonProperty("entrpsnm")
        private String entrpsnm;

        @JsonProperty("pcseguidance")
        private String pcseguidance;

        @JsonProperty("poster")
        private String poster;

        @JsonProperty("sty")
        private String sty;

        @JsonProperty("genrenm")
        private String genrenm;

        @JsonProperty("prfstate")
        private String prfstate;

        @JsonProperty("openrun")
        private String openrun;

        @JsonProperty("styurls")
        private StyUrls styurls;
    }

}
