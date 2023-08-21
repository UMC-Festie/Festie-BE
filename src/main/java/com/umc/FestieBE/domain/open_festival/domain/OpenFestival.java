package com.umc.FestieBE.domain.open_festival.domain;


import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.view.domain.View;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.DurationType;
import com.umc.FestieBE.global.type.OCategoryType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

import java.time.LocalDate;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class OpenFestival {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_festival_id")
    private String id;

//    //글
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user; //작성자

//    // 임시 유저 (테스트용)
//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "temporary_user_id", nullable = false)
//    private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private String festivalTitle; //공연, 축제 제목


    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String detailUrl;

    private Long view;
    private Long likes;
    private Long dislikes;

    @Column(nullable = false)
    private LocalDate startDate; //시작 날짜
    private LocalDate endDate; //끝나는 날짜
    private String startTime; //시작 시간
    private String durationTime; //총 시간
    private String adminsName;
    private String open;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    public void setOCategoryType(OCategoryType oCategoryType) {
        this.category = CategoryType.valueOf(oCategoryType.name());
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DurationType duration;

    @Enumerated(EnumType.STRING)
    private RegionType region;


    @OneToMany(fetch = LAZY, mappedBy = "openFestival")
    private List<LikeOrDislike> likeOrDislikes;

    @OneToMany(fetch = LAZY, mappedBy = "openFestival")
    private List<View> views;

    public OpenFestival() {

    }
}
