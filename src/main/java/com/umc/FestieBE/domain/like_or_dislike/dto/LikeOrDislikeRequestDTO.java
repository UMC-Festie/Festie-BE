package com.umc.FestieBE.domain.like_or_dislike.dto;

import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeOrDislikeRequestDTO {

    private Integer status; // 좋아요(1)/싫어요(0) 상태
    private Long festivalId;
    private Long ticketingId;
    private Long reviewId;


}
