package com.umc.FestieBE.domain.review.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.FetchType.LAZY;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
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
    private Long festivalId; // 연동한 공연/축제 식별자

    private String thumbnailUrl;

    //@ElementCollection // imagesUrl는 별도의 테이블에 매핑 -> image 도메인
    //private List<String> imagesUrl; // 업로드한 이미지 파일 url

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType festivalType;

    @Column(nullable = false)
    private LocalDate startDate; //시작 날짜
    @Column(nullable = false)
    private LocalDate endDate; //끝나는 날짜

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    @Column(nullable = false)
    private String festivalTitle;

    @OneToMany(fetch = LAZY, mappedBy = "review")
    private List<LikeOrDislike> likeOrDislikes;

}
