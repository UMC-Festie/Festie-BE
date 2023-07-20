package com.umc.FestieBE.domain.ticketing.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@Builder
@AllArgsConstructor
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

    // 임시 유저 (테스트용)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "temporary_user_id", nullable = false)
    private TemporaryUser temporaryUser;


    @Column(nullable = false)
    private String title; // *** ERD에 없는 내용 (추가)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long view;

    // 공연, 축제 정보가 연동된 경우 아래의 정보가 추가됨
    // [추가되는 정보] thumbnail, type (공연 or 축제), category, data, title
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private FestivalType type;

    // @Enumerated(EnumType.STRING)
    private Integer category; // Integer 보류

    private LocalDate date;
    private LocalTime time;
    private String festivalTitle;

    // ** [추가] 티겟팅 게시글 수정일
    private LocalDateTime modifyDate;
}
