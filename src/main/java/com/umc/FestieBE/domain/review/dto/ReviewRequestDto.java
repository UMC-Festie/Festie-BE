package com.umc.FestieBE.domain.review.dto;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.user.domain.User;
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
import java.time.LocalTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRequestDto {

    private String festivalId;
    private String boardType;

    @NotBlank(message = "공연/축제 제목은 필수 입력 값입니다.")
    private String festivalTitle;

    private String thumbnailUrl; /*축제, 공연 포스터 썸네일*/

    @NotNull(message = "공연/축제 유형은 필수 입력 값입니다. ") //IntegerType은 NotNull만 사용 가능하다.
    @Min(value = 0, message = "공연=0, 축제=1")
    @Max(value = 1, message = "공연=0, 축제=1")
    private Integer festivalType; /*공연인지 축제인지의 여부*/

    @NotNull(message = "공연/축제 카테고리는 필수 입력 값입니다.")
    @Min(value = 0, message = "공연/축제 카테고리는 0부터 8까지의 정수 값입니다.")
    @Max(value = 8, message = "공연/축제 카테고리는 0부터 8까지의 정수 값입니다.")
    private Integer categoryType;

    @NotBlank(message = "날짜는 필수 입력 값입니다.")
    private String date; //날짜
    private String time; //시간

    private String postTitle;

    @NotBlank(message = "후기 게시글은 필수 입력 값입니다.")
    private String content;

    // 연동 시
    public Review toEntity(User user, FestivalType festivalType, CategoryType categoryType,
                           String thumbnailUrl) {
        return Review.builder()
                //후기 게시글 정보
                .user(user)
                .view(0L)
                .title(postTitle)
                .content(content)
                //공연or축제 정보
                .festivalId(festivalId)
                .boardType(boardType)
                .thumbnailUrl(thumbnailUrl)
                .categoryType(categoryType)
                .festivalType(festivalType)
                .date(LocalDate.parse(date))
                .time(LocalTime.parse(time))
                .festivalTitle(festivalTitle)
                .build();
    }
}