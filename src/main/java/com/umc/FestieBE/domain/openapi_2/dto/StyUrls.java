package com.umc.FestieBE.domain.openapi_2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StyUrls {
    @JsonProperty("styurl")
    private List<String> urls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

}
