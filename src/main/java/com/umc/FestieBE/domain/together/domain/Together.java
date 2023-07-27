package com.umc.FestieBE.domain.together.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Together extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "together_id")
    private Long id;

    //@ManyToOne(fetch = LAZY)
    //@JoinColumn(name = "user_id", nullable = false)
    //private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "temporary_user_id", nullable = false)
    private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private Integer status; // 매칭 상태

    @Column(nullable = false)
    private Long view;

    // 공연 정보
    // 1. 연동할 경우
    //@ManyToOne(fetch = LAZY)
    //@JoinColumn(name = "festival_id")
    //private Festival festival;

    private Long festivalId; // 연동한 공연/축제 식별자

    // 2. 연동하지 않을 경우
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegionType region;

    @Column(nullable = false)
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


    // 같이가요 게시글 수정
    public void updateTogether(TogetherRequestDTO.TogetherRequest request){
        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        CategoryType categoryType = CategoryType.findCategoryType(request.getCategory());
        RegionType regionType = RegionType.findRegionType(request.getRegion());

        this.festivalId = request.getFestivalId();
        this.thumbnailUrl = request.getThumbnailUrl();
        this.type = festivalType;
        this.category = categoryType;
        this.region = regionType;
        this.festivalTitle = request.getFestivalTitle();
        this.date = LocalDate.parse(request.getTogetherDate());
        this.time = LocalTime.parse(request.getTogetherTime());
        this.title = request.getTitle();
        this.content = request.getContent();
        this.target = request.getTarget();
        this.message = request.getMessage();

    }
}
