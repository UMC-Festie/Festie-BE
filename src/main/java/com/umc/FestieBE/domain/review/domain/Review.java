package com.umc.FestieBE.domain.review.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //후기 글 작성
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long view;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    //공연 상세 정보 연동할 경우
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "festival_id")
    //private Festival festival;
    private String festivalId; // 연동한 공연/축제 식별자
    private String boardType; // 연동한 공연/축제 게시글 유형(정보보기/정보공유)

    //공연 상세 정보 연동 안 할 경우
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Column(nullable = false)
    private LocalDate date; // 날짜

    private LocalTime time; // 시간

    @Column(nullable = false)
    private String festivalTitle;

    @OneToMany(fetch = LAZY, mappedBy = "review")
    private List<LikeOrDislike> likeOrDislikes;

}
