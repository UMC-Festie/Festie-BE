package com.umc.FestieBE.domain.like_or_dislike.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeOrDislike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_or_dislike_id")
    private Long id;

    @Column(nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketing_id")
    private Ticketing ticketing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

}
