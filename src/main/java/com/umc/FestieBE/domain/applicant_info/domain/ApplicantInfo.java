package com.umc.FestieBE.domain.applicant_info.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ApplicantInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "applicant_info_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "together_id", nullable = false)
    private Together together;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String introduction;

    @Column(nullable = false)
    private Boolean isSelected;

}
