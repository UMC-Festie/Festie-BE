package com.umc.FestieBE.domain.review.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.review.dto.ReviewRequestDto;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private String festivalId; // 연동한 공연/축제 식별자
    private String boardType; // 연동한 공연/축제 게시글 유형(정보보기/정보공유)

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType festivalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    @Column(nullable = false)
    private LocalDate date; // 날짜

    private LocalTime time; // 시간

    @Column(nullable = false)
    private String festivalTitle;

    @Column(name = "likes")
    private Long likes = 0L;

    @Column(name = "dislikes")
    private Long dislikes = 0L;

    @ElementCollection // imagesUrl는 별도의 테이블에 매핑
    private List<String> imagesUrl; // 업로드한 이미지 파일 url

    public void incrementLikes() {
        this.likes++;
    }

    public void incrementDislikes() {
        this.dislikes++;
    }

    public void decrementLikes() {
        this.likes--;
    }

    public void decrementDislikes() {
        this.dislikes--;
    }
    // 연동된 축제/공연 정보 삭

    public void updateReview(ReviewRequestDto request, String imgUrl){
        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        CategoryType categoryType = CategoryType.findCategoryType(request.getCategoryType());
        this.festivalId = request.getFestivalId();
        this.thumbnailUrl = imgUrl;
        this.festivalType = festivalType;
        this.categoryType = categoryType;
        this.festivalTitle = request.getFestivalTitle();
        this.date = LocalDate.parse(request.getDate());
        this.time = LocalTime.parse(request.getTime());
        //연동 관련 정보들은 requestDto에서 온다.
        this.content = request.getContent();
    }

}
