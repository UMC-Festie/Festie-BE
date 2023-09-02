package com.umc.FestieBE.domain.user.application;

import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.token.RefreshTokenJpaRepository;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
//import com.umc.FestieBE.domain.user.dto.MailDto;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Transport;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
@Data
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private String refreshToken;

    @Transactional
    public Long signUp(UserSignUpRequestDto requestDto) //throws Exception
    {

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            //throw new Exception("이미 존재하는 이메일입니다.");
            throw new CustomException(EMAIL_ALREADY_EXIST);
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckPassword())) {
            //throw new Exception("비밀번호가 일치하지 않습니다.");
            throw new CustomException(PASSWORD_MISMATCH);
        }

        User user = userRepository.save(requestDto.toEntity());
        user.encodePassword(passwordEncoder);
        return user.getId();
    }



//    public String login(Map<String, String> users) //throws Exception
//    {
//
//        User user = userRepository.findByEmail(users.get("email"))
//                //.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 Email 입니다."));
//                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
//
//        String password = users.get("password");
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            //throw new Exception("잘못된 비밀번호입니다.");
//            throw new CustomException(LOGIN_FAILED);
//        }
//
//        List<String> roles = new ArrayList<>();
//        roles.add(user.getRole().name());
//        //refreshtoken 생성
//        refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), roles);
//
//
//
//
//        //사용자의 역할을 리스트에 추가해주는 것
//        return jwtTokenProvider.createToken(user.getEmail(), roles);
//        //access 토큰을 생성, 그리고 반환
//    }
}