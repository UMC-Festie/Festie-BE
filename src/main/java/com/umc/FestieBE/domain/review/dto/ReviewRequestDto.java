package com.umc.FestieBE.domain.review.dto;

import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static java.lang.Integer.parseInt;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRequestDto {

    private Long festivalId;

    @NotBlank(message = "공연/축제 제목은 필수 입력 값입니다.")
    private String festivalTitle;

    private String thumbnailUrl; /*축제, 공연 포스터 썸네일*/

    @NotBlank(message = "공연/축제 유형은 필수 입력 값입니다. ")
    @Min(value = 0, message = "공연=0, 축제=1")
    @Max(value = 1, message = "공연=0, 축제=1")
    private Integer festivalType; /*공연인지 축제인지의 여부*/

    @NotNull(message = "공연/축제 카테고리는 필수 입력 값입니다.")
    @Min(value = 0, message = "공연/축제 카테고리는 0부터 8까지의 정수 값입니다.")
    @Max(value = 8, message = "공연/축제 카테고리는 0부터 8까지의 정수 값입니다.")
    private Integer categoryType;

    @NotNull(message = "공연/축제 시작일은 필수 입력 값입니다.")
    private String startDate;

    @NotNull(message = "공연/축제 종료일은 필수 입력 값입니다.")
    private String endDate;

    private String postTitle;

    @NotBlank(message = "후기 게시글은 필수 입력 값입니다.")
    private String postContent;

    // 연동 시
    public Review toEntity(User user, FestivalType festivalType, CategoryType categoryType,
                           String thumbnailUrl) {
        return Review.builder()
                //후기 게시글 정보
                .user(user)
                .view(0L)
                .title(postTitle)
                .content(postContent)
                //공연or축제 정보
                .festivalId(festivalId)
                .thumbnailUrl(thumbnailUrl)
                .categoryType(categoryType)
                .festivalType(festivalType)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .festivalTitle(festivalTitle)
                .build();
    }
}