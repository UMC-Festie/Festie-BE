package com.umc.FestieBE.domain.festival.api;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festival")
public class FestivalController {
    private final FestivalService festivalService;

    @PostMapping("")
    public ResponseEntity<Void> createFestival(@Valid @RequestBody FestivalRequestDTO.FestivalRequest request) {
        festivalService.createFestival(request);
        return ResponseEntity.ok().build();
    }
}
