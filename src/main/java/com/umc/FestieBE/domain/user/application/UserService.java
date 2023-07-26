package com.umc.FestieBE.domain.user.application;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor //not null 객체를 위한 생성자
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @Transactional
    public Long signUp(UserSignUpRequestDto requestDto) throws Exception {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new Exception("중복된 이메일 입니다."); //중복된 이메일 처리
        }

        if(!requestDto.getPassword().equals(requestDto.getCheckPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다."); // 비밀번호 재확인 불일치성 처리
        }

        User user = userRepository.save(requestDto.toEntity()); //requestDto에서 받은 toEntity()정보들을 DB에 저장해준다.
        user.encodePassword(passwordEncoder); //비밀번호를 암호화해준다.
        return user.getId();//식별자를 기준으로 반환받는다.
    }
    @Transactional
    public String logIn(Map<String, String> users) {
        User user = userRepository.findByEmail(users.get("email")).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        String password = users.get("password");
        if(!user.checkPassword(passwordEncoder, password)) {
            throw new IllegalArgumentException(("잘못된 비밀번호입니다."));
        }
        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().name());


        return jwtTokenProvider.createToken(user.getUsername(), roles);
    }



}



