package com.umc.FestieBE.domain.together.dto;

import lombok.Getter;

@Getter
public class SearchResponseDTO {
    private String thumbnailUrl;
    private String title;
    private String content;
    private String updatedAt;
    private Long view;
    private Long likeCount;
}
