package com.umc.FestieBE.domain.mypage.domain;


import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Mypage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mypage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Integer age;



    // TODO 티켓팅 조회 내역 불러오는 중
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "recently_viewed_ticketing",
            joinColumns = @JoinColumn(name = "mypage_id"),
            inverseJoinColumns = @JoinColumn(name = "ticketing_id"))
    private List<Ticketing> recentlyViewedTicketings; // 최근에 본 티켓팅 내역

    public void updateRecentTicketing(List<Ticketing> recentTicketings) {
        this.recentlyViewedTicketings= recentTicketings;
    }
}
