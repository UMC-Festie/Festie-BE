package com.umc.FestieBE.domain.ticketing.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticketing extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketing_id", unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title; // *** ERD에 없는 내용 (추가)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long view;

    // 공연, 축제 정보가 연동된 경우 아래의 정보가 추가됨
    // [추가되는 정보] thumnail, type (공연 or 축제), category, data, title
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private FestivalType type; // *** ERD에 없는 내용 (추가)

    // @Enumerated(EnumType.STRING)
    private Integer category; // Integer 보류

    private LocalDate date;
    private LocalTime time;
    private String festivalTitle;
}
