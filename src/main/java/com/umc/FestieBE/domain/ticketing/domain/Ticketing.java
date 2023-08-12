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
import java.util.List;

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

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "festival_id")
    // private Festival festival;
    private String festivalId;
    private String boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 임시 유저 (테스트용)
    //@ManyToOne(fetch = LAZY)
    //@JoinColumn(name = "temporary_user_id", nullable = false)
    //private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private String title; // *** ERD에 없는 내용 (추가)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long view;

    // 공연, 축제 정보가 연동된 경우 -> 썸네일, 축제/공연 제목 가져옴
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private FestivalType type;

    // @Enumerated(EnumType.STRING)

    private LocalDate ticketingDate;
    private LocalTime ticketingTime;
    private String festivalTitle;

    @ElementCollection // imagesUrl는 별도의 테이블에 매핑
    private List<String> imagesUrl; // 업로드한 이미지 파일 url

    private Long likes; // 좋아요


    // [티켓팅 수정]에 필요한 Entity 추가 구현
    public void updateTicketing(String festivalId,
                                String festivalTitle,
                                String thumbnailUrl,
                                LocalDate ticketingDate,
                                LocalTime ticketingTime,
                                String title,
                                String content,
                                List<String> imagesUrl) {
        this.festivalId = festivalId;
        this.festivalTitle = festivalTitle;
        this.thumbnailUrl = thumbnailUrl;
        this.ticketingDate = ticketingDate;
        this.ticketingTime = ticketingTime;
        this.title = title;
        this.content = content;
        this.imagesUrl = imagesUrl;
    }

    // 연동된 공연, 축제 정보 삭제 시 필요
    public void clearFestivalInfo() {
        this.festivalId = null;
        this.festivalTitle = null;
        this.thumbnailUrl = null;
    }

    // 좋아요수 반영
    public void addLikes(Long likes) {
        this.likes = likes;
    }
}
