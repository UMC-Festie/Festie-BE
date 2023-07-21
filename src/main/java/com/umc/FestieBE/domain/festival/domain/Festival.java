package com.umc.FestieBE.domain.festival.domain;


import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Festival extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "festival_id")
    private Long id;

    //글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //작성자

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
    private String startDate; //시작 날짜

    private String endDate; //끝나는 날짜
    private String startTime; //시작 시간
    private String durationTime; //총 시간

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

    @Column(nullable = false)
    private Boolean isDeleted;

}
