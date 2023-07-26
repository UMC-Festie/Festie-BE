package com.umc.FestieBE.domain.user.api;

import com.umc.FestieBE.domain.user.application.UserService;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public Long join(@Valid @RequestBody UserSignUpRequestDto request) throws Exception {
        System.out.println("Suceess!");
        return userService.signUp(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        return userService.logIn(user);
    }




}
