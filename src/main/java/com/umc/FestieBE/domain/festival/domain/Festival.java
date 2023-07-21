package com.umc.FestieBE.domain.festival.domain;


import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static javax.persistence.FetchType.LAZY;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Festival extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "festival_id")
    private Long id;

    //글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //작성자

    // 임시 유저 (테스트용)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "temporary_user_id", nullable = false)
    private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private String festivalTitle;

    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String detailUrl;

    @Column(nullable = false)
    private Long view;
    
    //글 세부내용
    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private LocalDate startDate; //시작 날짜
    private LocalDate endDate; //끝나는 날짜

    private LocalTime startTime; //시작 시간
    private Integer durationTime; //총 시간 (단위: 분)

    private String adminsName;
    private String adminsPhone;
    private String adminsSiteAddress;

    //카테고리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType type;

    //@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Integer category; //보류

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegionType region;

}
