package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.TogetherDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TogetherController {

    private final TogetherService togetherService;

    @PostMapping("/together")
    public ResponseEntity<Void> createTogether(@Valid @RequestBody TogetherDTO.TogetherRequest request) {
        togetherService.createTogether(request);
        return ResponseEntity.ok().build();
    }

}
