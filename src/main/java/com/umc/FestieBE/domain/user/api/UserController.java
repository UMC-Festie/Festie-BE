package com.umc.FestieBE.domain.user.api;

import com.umc.FestieBE.domain.user.application.UserService;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository memberRepository;
    private Object HttpStatus;

    @PostMapping("/signup")
    @ResponseStatus(org.springframework.http.HttpStatus.OK)
    public Long join(@Valid @RequestBody UserSignUpRequestDto request) throws Exception {
        return userService.signUp(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) throws Exception {
        return userService.login(user);
    }
}
