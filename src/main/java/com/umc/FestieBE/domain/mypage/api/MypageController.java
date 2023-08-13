package com.umc.FestieBE.domain.mypage.api;

import com.umc.FestieBE.domain.mypage.application.MypageService;
import com.umc.FestieBE.domain.mypage.dto.MypageResponseDTO;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    @GetMapping("/mypage")
    public ResponseEntity<MypageResponseDTO.MypageUserResponse> getMypage(@AuthenticationPrincipal User user) {
        MypageResponseDTO.MypageUserResponse mypageResponse = mypageService.getMypage(user);

        return ResponseEntity.ok(mypageResponse);
    }
}

