package com.umc.FestieBE.domain.review.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;


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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    //공연 상세 정보 연동 안 할 경우
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private FestivalType type;

    private Integer category;

    @Column(nullable = false)
    private LocalDate date;

    private String festivalTitle;
}
