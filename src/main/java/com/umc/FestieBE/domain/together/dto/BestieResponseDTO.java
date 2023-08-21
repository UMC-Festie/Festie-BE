package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.together.domain.Together;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class BestieResponseDTO {
    private String title;
    private String updatedAt;
    private String thumbnailUrl;
    private String writerNickname;
    private String isApplicationSuccess;

    public BestieResponseDTO(Together together, String isApplicationSuccess){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String updatedDate = together.getUpdatedAt().format(formatter);

        this.title = together.getTitle();
        this.updatedAt = updatedDate;
        this.thumbnailUrl = together.getThumbnailUrl();
        this.writerNickname = together.getUser().getNickname();
        this.isApplicationSuccess = isApplicationSuccess;
    }
}
