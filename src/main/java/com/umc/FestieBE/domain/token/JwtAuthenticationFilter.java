package com.umc.FestieBE.domain.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtTokenProvider jwtAuthenticationProvider;
    public JwtAuthenticationFilter(JwtTokenProvider provider) {
        jwtAuthenticationProvider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtAuthenticationProvider.resolveToken(request);
        //jwtAuthenticationProvider 객체를 사용하여 토큰을 추출해준다.
        //resolveToken 메서드는 request의 헤더나, 파라미터에서 jwt 토큰을 찾아내고, 이를 문자열로 반환해준다.

         if( token != null && jwtAuthenticationProvider.validateToken(token)) { //위에서 검증된 토큰이 유효하면 if문 실행
            //토큰이 null값이 아니고, jwtAuthenticationProvider를 사용하여서 토큰을 검증해준다.
            //validateToken은 JWT토큰의 sign을 확인하고, 유효성을 검증한다 -> 토큰의 변조, 만료성을 확인해준다.
            Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);
            //jwtAuthenticationProvider를 통해 token을 제공받아 Authentication 객체를 생성한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //SecurityContextHolder는 기본적으로 Spring framework이며, 현재 스레드의 보안 컨텍스트를 유지해준다.
            //setAuthentication을 호출하여 이전단계에서 얻은 authentication 객체를 현재 현재 사용자의 인증정보로 설정한다.

            //따라서 이 코드에선 if문에서 제공된 token(not null)이 jwtAuthenticationProvider를 사용하여 먼저 토큰의 유효성 검사를 해준다.
            //두 조건(not null, validation)이 만족되면 token에서 사용자 정보를 추출하고, 해당 사용자를 보안 컨텍스트에 인증된 사용자로 설정하여
            //응용 프로그램 내 보호된 리소스나, 작업 등을 수행할 수 있게 끔 한다.
        }


        filterChain.doFilter(request, response);
    }
}
