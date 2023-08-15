package com.umc.FestieBE.domain.open_performance.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;


@Getter
public class OpenPerformanceDTO {

            @JsonProperty("mt20id")
            private String mt20id;

            @JsonProperty("prfnm")
            private String prfnm;

            @JsonProperty("prfpdfrom")
            private LocalDate prfpdfrom;

            @JsonProperty("prfpdto")
            private LocalDate prfpdto;

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


}
