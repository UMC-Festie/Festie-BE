package com.umc.FestieBE.domain.user.application;

import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(UserSignUpRequestDto requestDto) throws Exception {

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckPassword())){
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.save(requestDto.toEntity());
        user.encodePassword(passwordEncoder);
        return user.getId();
    }
}
