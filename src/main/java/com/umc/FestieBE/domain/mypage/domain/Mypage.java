package com.umc.FestieBE.domain.mypage.domain;


import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
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


    // TODO 정보보기

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    private List<Festival> festival; // 정보공유

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private List<Review> review; // 후기

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketing_id")
    private List<Ticketing> ticketing; // 티켓팅

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "together_id")
    private List<Together> together; // 같이가요
}
