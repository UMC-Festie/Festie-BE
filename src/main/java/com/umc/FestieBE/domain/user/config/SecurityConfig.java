package com.umc.FestieBE.domain.user.config;

import com.umc.FestieBE.domain.token.JwtAuthenticationFilter;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //기본적인 web 보안을 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable() // jwt 방식을 사용하기에 formLogin은 무시해준다.
                .httpBasic().disable()
                .csrf().disable() //token을 사용하기에 제외
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt로 구현할 땐 세션 사용 X
                .and()
                .authorizeRequests()//httpSevletRequest를 사용하기 위해, 그리고 요청에 대한 권한을 지정한다.
                .antMatchers("/user/signup").permitAll() //모든 사용자에게 /signup 권한 허용
                .antMatchers("/user/login").permitAll()// "" /login ""
                .antMatchers("/user").hasRole("USER")
                .antMatchers("/hello").permitAll()
                .antMatchers(HttpMethod.GET, "/together/*").permitAll()
                .antMatchers(HttpMethod.POST, "/together/*").hasRole("USER")
                .antMatchers("/base/*").permitAll()
                .antMatchers(HttpMethod.GET,"/ticketing/*").permitAll()
                .antMatchers(HttpMethod.GET,"/calendar/*").permitAll()
                .antMatchers(HttpMethod.GET,"/festival/*").permitAll()
                .antMatchers(HttpMethod.GET, "/performance/*").permitAll()
                .antMatchers("/search").permitAll()
                .antMatchers("/home").permitAll()
                .anyRequest().authenticated()//위에서 설정한 경로 제외하고는, 모두 인증된 사용자만 접근 가능, 따라서 사용자는 회원가입, 로그인 전에는 다른 기능들을 사용 못한다.
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}