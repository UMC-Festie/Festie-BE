package com.umc.FestieBE.domain.applicant_info.api;

import com.umc.FestieBE.domain.applicant_info.application.ApplicantInfoService;
import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoBestieListDTO;
import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoRequestDTO;

import com.umc.FestieBE.domain.together.dto.BestieResponseDTO;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ApplicantInfoController {

    private final ApplicantInfoService applicantInfoService;

    @PostMapping("/together/bestie/application")
    public ResponseEntity<Void> createBestieApplication(
            @Valid @RequestBody ApplicantInfoRequestDTO.BestieApplicationRequest request
    ){
        applicantInfoService.createBestieApplication(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/together/bestie/choice")
    public ResponseEntity<Void> createBestieChoice(
            @Valid @RequestBody ApplicantInfoRequestDTO.BestieChoiceRequest request
    ){
        applicantInfoService.createBestieChoice(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bestie")
    public ResponseEntity<ApplicantInfoBestieListDTO> getRecentApplicantInfo(@RequestParam Integer page,
                                                                             @AuthenticationPrincipal User user)
    {
        ApplicantInfoBestieListDTO applicantInfoBestieListDTO = applicantInfoService.fetchRecentApplicantInfo(page, user);
        return ResponseEntity.ok(applicantInfoBestieListDTO);
    }
}
