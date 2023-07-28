package com.umc.FestieBE.domain.festival.api;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festival")
public class FestivalController {
    private final FestivalService festivalService;
    private final FestivalRepository festivalRepository;

    /** 새로운 공연/축제 등록 */
    @PostMapping("")
    public ResponseEntity<Void> createFestival(@Valid @RequestBody FestivalRequestDTO request) {
        festivalService.createFestival(request);
        return ResponseEntity.ok().build();
    }

    /** 새로운 공연/축제 수정 */
    @PutMapping("/{festivalId}")
    public ResponseEntity<Void> updateFestival(@PathVariable("festivalId") Long festivalId,
                                               @Valid @RequestBody FestivalRequestDTO request) {
        festivalService.updateFestival(festivalId, request);
        return ResponseEntity.ok().build();
    }


    /** 새로운 공연/축제 삭제
     * : isDeleted값만 true로 변경
     * */
    @PatchMapping("/{festivalId}")
    public ResponseEntity<Void> deleteFestival(@PathVariable("festivalId") Long festivalId,
                                               @RequestBody FestivalRequestDTO request) {
        Boolean isDeleted = request.getIsDeleted(); // 클라이언트로부터 전달된 isDeleted 값

        festivalService.deleteFestival(festivalId, isDeleted);
        return ResponseEntity.ok().build();
    }
}
