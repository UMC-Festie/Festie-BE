package com.umc.FestieBE.domain.open_festival.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class OpenFestivalDTO {

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

            @JsonProperty("festival")
            private String festival;


}
