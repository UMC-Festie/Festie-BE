package com.umc.FestieBE.domain.open_performance.domain;

import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_performance_id")
    private Long id;

//    //글
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user; //작성자

    // 임시 유저 (테스트용)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "temporary_user_id", nullable = false)
    private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private String festivalTitle; //공연, 축제 제목

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
    private String startDate; //시작 날짜

    private String endDate; //끝나는 날짜
    private String startTime; //시작 시간
    private String durationTime; //총 시간

    private String adminsName;

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
