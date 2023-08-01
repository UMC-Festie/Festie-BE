package com.umc.FestieBE.domain.user.application;

import com.umc.FestieBE.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public abstract class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { //loadUserByUsername은 UserDetails에 포함 되어있는 메소드이다ㅣ
        return (UserDetails) userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        //실제로는 사용자를 이메일로 판별하기때문에 username을 findByEmail에 대입해준다.
    }

}
