package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TogetherController {

    private final TogetherService togetherService;

    @PostMapping("/together")
    public ResponseEntity<Void> createTogether(@Valid @RequestBody TogetherRequestDTO.TogetherRequest request) {
        togetherService.createTogether(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/together/{togetherId}")
    public ResponseEntity<TogetherResponseDTO> getTogether(
            @PathVariable("togetherId") Long togetherId
    ){
        return ResponseEntity.ok().body(togetherService.getTogether(togetherId));
    }

    @PostMapping("/together/bestie/application")
    public ResponseEntity<Void> createBestieApplication(
            @Valid @RequestBody TogetherRequestDTO.BestieApplicationRequest request
    ){
        togetherService.createBestieApplication(request);
        return ResponseEntity.ok().build();
    }

}
