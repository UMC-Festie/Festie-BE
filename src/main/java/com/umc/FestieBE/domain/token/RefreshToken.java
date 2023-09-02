package com.umc.FestieBE.domain.token;

import com.umc.FestieBE.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Table(name = "T_REFRESH_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", nullable = false)
    private Long refreshTokenId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "email", nullable = false)
    private String keyEmail;

}
