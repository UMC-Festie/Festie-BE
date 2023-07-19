package com.umc.FestieBE.domain.calendar.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id", unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 임시 유저 (테스트용)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "temporary_user_id", nullable = false)
    private TemporaryUser temporaryUser;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate calendarDate;
}
