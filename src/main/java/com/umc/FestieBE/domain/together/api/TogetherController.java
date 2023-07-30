package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalSearchResponseDTO;
import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.HomeResponseDTO;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TogetherController {

    private final TogetherService togetherService;

    @PostMapping("/together")
    public ResponseEntity<Void> createTogether(
            @Valid @RequestBody TogetherRequestDTO.TogetherRequest request
    ){
        togetherService.createTogether(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/together/{togetherId}")
    public ResponseEntity<TogetherResponseDTO.TogetherDetailResponse> getTogether(
            @PathVariable("togetherId") Long togetherId
    ){
        return ResponseEntity.ok().body(togetherService.getTogether(togetherId));
    }

    @PatchMapping("/together/{togetherId}")
    public ResponseEntity<Void> updateTogether(
            @PathVariable("togetherId") Long togetherId,
            @Valid @RequestBody TogetherRequestDTO.TogetherRequest request
    ){
        togetherService.updateTogether(togetherId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/together/{togetherId}")
    public ResponseEntity<Void> deleteTogether(
            @PathVariable("togetherId") Long togetherId
    ){
        togetherService.deleteTogether(togetherId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/together")
    public ResponseEntity<TogetherResponseDTO.TogetherListResponse> getTogetherList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "region", required = false) Integer region,
            @RequestParam(value = "status", required = false, defaultValue = "0") Integer status, // 모집 중
            @RequestParam(value = "sortBy", required = false, defaultValue = "0") Integer sort // 최신 순
    ){
        return ResponseEntity.ok().body(togetherService.getTogetherList(page, type, category, region, status, sort));
    }

    @GetMapping("festival/search")
    public ResponseEntity<FestivalSearchResponseDTO.FestivalListResponse> getFestivalSearchList(
            @RequestParam(value = "keyword") String keyword
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalSearchList(keyword));
    }

    @GetMapping("festival/search/{festivalId}")
    public ResponseEntity<FestivalSearchResponseDTO.FestivalInfoResponse> getFestivalSelectedInfo(
            @PathVariable("festivalId") Long festivalId
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalSelectedInfo(festivalId));
    }

    @GetMapping("festie")
    public ResponseEntity<HomeResponseDTO> getFestivalAndTogetherList(
            @RequestParam(value = "festivalType", defaultValue = "0") int festivalType,
            @RequestParam(value = "togetherType", defaultValue = "0") int togetherType
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalAndTogetherList(festivalType, togetherType));
    }

}
