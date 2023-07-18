package com.umc.FestieBE.domain.user.application;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor //not null 객체를 위한 생성자

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    }