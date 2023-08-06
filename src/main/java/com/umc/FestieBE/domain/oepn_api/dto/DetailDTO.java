package com.umc.FestieBE.domain.oepn_api.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

import java.util.List;


@Getter
@JacksonXmlRootElement(localName = "dbs")
public class DetailDTO {
        @JacksonXmlProperty(localName = "mt20id")
        private String mt20id;

        @JacksonXmlProperty(localName = "prfnm")
        private String prfnm;

        @JacksonXmlProperty(localName = "prfpdfrom")
        private String prfpdfrom;

        @JacksonXmlProperty(localName = "prfpdto")
        private String prfpdto;

        @JacksonXmlProperty(localName = "fcltynm")
        private String fcltynm;

        @JacksonXmlProperty(localName = "prfcast")
        private String prfcast;

        @JacksonXmlProperty(localName = "prfcrew")
        private String prfcrew;

       @JacksonXmlProperty(localName = "dtguidance")
       private String dtguidance;

        @JacksonXmlProperty(localName = "prfruntime")
        private String prfruntime;

        @JacksonXmlProperty(localName = "prfage")
        private String prfage;

        @JacksonXmlProperty(localName = "entrpsnm")
        private String entrpsnm;

        @JacksonXmlProperty(localName = "pcseguidance")
        private String pcseguidance;

        @JacksonXmlProperty(localName = "poster")
        private String poster;

        @JacksonXmlProperty(localName = "sty")
        private String sty;

        @JacksonXmlProperty(localName = "genrenm")
        private String genrenm;

        @JacksonXmlProperty(localName = "prfstate")
        private String prfstate;

        @JacksonXmlProperty(localName = "openrun")
        private String openrun;

        @JacksonXmlProperty(localName = "styurls")
        private List<String> styurls;

        @JacksonXmlProperty(localName = "mt10id")
        private String mt10id;

}
