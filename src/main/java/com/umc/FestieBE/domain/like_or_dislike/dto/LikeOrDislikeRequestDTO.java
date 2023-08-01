package com.umc.FestieBE.domain.like_or_dislike.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class LikeOrDislikeRequestDTO {
    @NotNull(message = "좋아요 또는 싫어요 상태는 필수 입력값입니다.")
    private Integer status; // 좋아요(1)/싫어요(0) 상태

    private Long festivalId;
    private Long ticketingId;
    private Long reviewId;

    public LikeOrDislike toEntity(TemporaryUser tempUser, Festival festival, Ticketing ticketing, Review review){
        return LikeOrDislike.builder()
                .temporaryUser(tempUser)
                .status(status)
                .festival(festival)
                .ticketing(ticketing)
                .review(review)
                .build();
    }

}
