package com.umc.FestieBE.domain.festival.domain;


import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.umc.FestieBE.global.exception.CustomErrorCode.FESTIVAL_NOT_FOUND;
import static com.umc.FestieBE.global.type.FestivalType.*;
import static com.umc.FestieBE.global.type.RegionType.findRegionType;
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
    //@ManyToOne(fetch = LAZY)
    //@JoinColumn(name = "temporary_user_id", nullable = false)
    //private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private String festivalTitle; // 공연, 축제 제목

    private String title; // 사용자가 작성한 제목

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String reservingUrl;

    @Column(nullable = false)
    private Long view;

    //글 세부내용
    private String reservationLink; // 예매 링크

    @Column(nullable = false)
    private LocalDate startDate; //시작 날짜
    @Column(nullable = false)
    private LocalDate endDate; //끝나는 날짜

    // 시작 시간 (0723 -> 시작 시간만 표기하는걸로 결정됨 ex. '18:00 ~ ' 이런식으로 표기)
    @Column(nullable = false)
    private LocalTime startTime;

    private String adminsName;
    private String adminsPhone;
    private String adminsSiteAddress;

    //카테고리
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
    private Boolean isDeleted;

    private String duration;

    @ElementCollection // imagesUrl는 별도의 테이블에 매핑
    private List<String> imagesUrl; // 업로드한 이미지 파일 url

    @Column(nullable = false)
    private String thumbnailUrl;

    private Long likes;

    // 새로운 공연, 축제 [수정]에 사용되는 메서드
    public void updateFestival(String festivalTitle,

                                FestivalType festivalType,
                                CategoryType category,
                                RegionType region,
                                String location,
                                LocalDate startDate,
                                LocalDate endDate,
                                LocalTime startTime,
                                String reservationLink,
                                String title,
                                String content,
                                String adminsName,
                                String adminsPhone,
                                String adminsSiteAddress,
                                Boolean isDeleted,
                                List<String> imagesUrl,
                                String thumbnailUrl
                                ) {
/*
                               FestivalType festivalType,
                               CategoryType category,
                               RegionType region,
                               String location,
                               LocalDate startDate,
                               LocalDate endDate,
                               LocalTime startTime,
                               String reservationLink,
                               String title,
                               String content,
                               String adminsName,
                               String adminsPhone,
                               String adminsSiteAddress,
                               Boolean isDeleted,
                               List<String> imagesUrl,
                               String thumbnailUrl
    ) {
*/
        this.festivalTitle = festivalTitle;
        this.type = festivalType;
        this.category = category;
        this.region = region;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.reservationLink = reservationLink;
        this.title = title;
        this.content = content;
        this.adminsName = adminsName;
        this.adminsPhone = adminsPhone;
        this.adminsSiteAddress = adminsSiteAddress;
        this.isDeleted = isDeleted;
        this.imagesUrl = imagesUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void deleteFestival(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void addLikes(Long likes) {
        this.likes = likes;
    }
}
