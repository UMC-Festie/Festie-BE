package com.umc.FestieBE.domain.user.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static String getLoginUserName() { //사용자의 정보를 찾아준다
        //UserDetails는 사용자의 정보를 담은 스프링의 인터페이스이다.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername(); //getUserName은 계정의 고유값(PK)를 반환한다.
    }
}
