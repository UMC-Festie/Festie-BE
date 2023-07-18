package com.umc.FestieBE.domain.temporary_user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporaryUserService {

    private final TemporaryUserRepository temporaryUserRepository;

    public TemporaryUser createTemporaryUser(){
        TemporaryUser temporaryUser = TemporaryUser.builder()
                .nickname("lee")
                .email("lee@naver.com")
                .password("123456")
                .gender('F')
                .age(25)
                .build();
        TemporaryUser tempUser = temporaryUserRepository.save(temporaryUser);
        return tempUser;
    }
}
