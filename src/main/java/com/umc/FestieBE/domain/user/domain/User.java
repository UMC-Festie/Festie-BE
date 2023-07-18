package com.umc.FestieBE.domain.user.domain;

import com.umc.FestieBE.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Character gender; //남자면 M, 여자면 F

    @Column(nullable = false)
    private LocalDate birthday = LocalDate.now();


    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
