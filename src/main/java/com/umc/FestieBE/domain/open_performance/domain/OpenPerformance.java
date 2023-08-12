package com.umc.FestieBE.domain.open_performance.domain;

import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.type.*;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class OpenPerformance {

    @Id
    @Column(name = "open_performance_id")
    private String id;

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
    private String startDate; //시작 날짜
    private String endDate; //끝나는 날짜
    private String startTime; //시작 시간
    private String durationTime; //총 시간

    private String adminsName;
    private String openrun;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OCategoryType category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DurationType duration;

    @Enumerated(EnumType.STRING)
    private RegionType region; //보류


    public OpenPerformance() {

    }

}
