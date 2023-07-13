package com.umc.FestieBE.domain.together.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
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

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Together extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "together_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer status; // 매칭 상태

    @Column(nullable = false)
    private Long view;

    // 공연 정보
    // 1. 연동할 경우
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    // 2. 연동하지 않을 경우
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private FestivalType type;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    private RegionType region;

    private String festivalTitle;

    // Together 게시글 정보
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String target;

    @Column(nullable = false)
    private String message;

}
