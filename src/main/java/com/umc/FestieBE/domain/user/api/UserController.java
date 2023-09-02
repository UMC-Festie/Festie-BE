package com.umc.FestieBE.domain.user.api;

//import com.umc.FestieBE.domain.user.application.MailService;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.token.TokenDto;
import com.umc.FestieBE.domain.user.application.UserService;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
//import com.umc.FestieBE.domain.user.dto.MailDto;
import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

import static com.umc.FestieBE.global.exception.CustomErrorCode.EMAIL_NOT_FOUND;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private Object HttpStatus;
    //private MailService ms;

    //@PostMapping("/signup")
    //@ResponseStatus(org.springframework.http.HttpStatus.OK)
    //public Long join(@Valid @RequestBody UserSignUpRequestDto request) throws Exception {
    //    return userService.signUp(request);
    //}

    @PostMapping("/signup")
    public ResponseEntity<Void> join(@Valid @RequestBody UserSignUpRequestDto request) {
        userService.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) throws Exception {
        return userService.login(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> user) {
        String token = userService.login(user);

        return ResponseEntity.ok().body(token);
    }

//    @PostMapping("/login")
//    public TokenDto login(@RequestBody Map<String, String> user) {
//        log.info("user email = {}", user.get("userEmail"));
//        User loginUser = userRepository.findByEmail(user.get("email"))
//                .orElseThrow(() -> new CustomException(EMAIL_NOT_FOUND));
//
//        TokenDto tokenDto = jwtTokenProvider.createAccessToken(user.getPk(), roles);
//        jwtService.login(tokenDto);
//        return tokenDto;
//    }
//}

   /* @Transactional
    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam("email") User email){
        MailDto dto = ms.createMailAndChangePassword(String.valueOf(email));
        .mailSend(dto);
        return "/user/login";
}*/