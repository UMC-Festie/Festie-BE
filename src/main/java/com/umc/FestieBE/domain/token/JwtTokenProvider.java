package com.umc.FestieBE.domain.token;

import com.umc.FestieBE.domain.user.application.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";

    //토큰 유효시간 168시간(7일)
    private Long tokenVaildTime = 1440 * 60 * 7 * 1000L;
    private final UserDetailsService userDetailsService;

    //private final CustomUserDetailsService ustomUserDetailsService;

    //객체를 초기화 하고, secretKey를 Base64로 인코딩한다.
    @PostConstruct // 의존성 주입이 완료된 후에 실행되어야 하는 method에 사용
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //jwt 토큰 생성
    public String createToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); //Jwt payload에 저장되는 정보단위
        claims.put("roles", roles); //저장되는 정보의 단위는 key, value 형식으로 저장된다.
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) //정보저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenVaildTime)) // 토큰 만료 시간 정보
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘, 그리고 signature에 들어갈 secretket값을 세팅해준다.
                .compact();
    }

    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //토큰에서 회원정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //Request의 헤더에서 토큰값 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN"); //X-AUTH-TOKEN이란, 한개의 요청(토큰)으로 여러 승인을 요청하는 것
    }

    //토큰의 유효성, 완료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            //jwts.parser() ->Jwt를 parsing(해독)하는 객체를 생성해준다.
            //setSigningKey -> 검증에 사용할 서명키를 생성, secretKey가 서명키로 사용된다.
            //parseClaimsJws -> 주어진 jwtToken을 검증하고, 내용을 해석하여, 헤더와 payload를 추출한다.
            return !claims.getBody().getExpiration().before(new Date());
            //토큰의 만료시간이 현재 시간 이 후 이면, 토큰은 유효하니 true를 반환하고, 만료시간이 현재시간 이 전이면, false를 반환한다.(토큰이 만료되었다는 말)
        } catch (Exception e) {
            return false;
        }

    }

}
