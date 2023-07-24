package com.umc.FestieBE.domain.festival.api;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festival")
public class FestivalController {
    private final FestivalService festivalService;

    // [새로운 공연/축제 등록]
    @PostMapping("")
    public ResponseEntity<Void> createFestival(@Valid @RequestBody FestivalRequestDTO request) {
        festivalService.createFestival(request);
        return ResponseEntity.ok().build();
    }

    // [새로운 공연/축제 삭제] -> 기능상 내용 삭제 X, 수정되어야 해서 PutMapping 사용
    @PutMapping("/{festivalId}")
    public ResponseEntity<Void> deleteFestival(@PathVariable Long festivalId,
                                               @Valid @RequestBody FestivalRequestDTO request) {
        festivalService.deleteFestival(festivalId, request);
        return ResponseEntity.ok().build();
    }
}
