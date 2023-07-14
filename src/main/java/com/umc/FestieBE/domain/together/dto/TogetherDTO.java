package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

public class TogetherDTO {
    @Getter
    public static class TogetherRequest {
        // 축제 정보
        private Long festivalId;

        private String thumbnailUrl;
        private String festivalTitle;
        private Integer festivalType;
        private Integer category;
        private String region;

        // 같이가요 게시글 정보
        private String togetherDate;
        private String togetherTime;

        private String title;
        private String content;
        private String target;
        private String message;

        // DTO -> Entity
        public Together toEntity(TemporaryUser tempUser, FestivalType festivalType, RegionType region){
            return buildCommonProperties()
                    .temporaryUser(tempUser)
                    .thumbnailUrl(thumbnailUrl)
                    .festivalTitle(festivalTitle)
                    .type(festivalType)
                    .region(region)
                    // 카테고리
                    .build();
        }

        public Together toEntity(TemporaryUser tempUser, Festival festival){
            return buildCommonProperties()
                    .temporaryUser(tempUser)
                    .festival(festival)
                    .build();
        }

        private Together.TogetherBuilder buildCommonProperties(){
            return Together.builder()
                    .status(0) // 매칭 대기 중
                    .view(0L)
                    .date(LocalDate.parse(togetherDate))
                    .time(LocalTime.parse(togetherTime))
                    .title(title)
                    .content(content)
                    .target(target)
                    .message(message);
        }

    }
}
