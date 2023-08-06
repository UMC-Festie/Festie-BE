package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TogetherController {

    private final TogetherService togetherService;

    @PostMapping("/together")
    public ResponseEntity<Void> createTogether(
            @Valid @RequestPart(value = "data") TogetherRequestDTO.TogetherRequest request,
            @RequestPart(value = "thumbnail") MultipartFile thumbnail
    ){
        togetherService.createTogether(request, thumbnail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/together/{togetherId}")
    public ResponseEntity<TogetherResponseDTO.TogetherDetailResponse> getTogether(
            @PathVariable("togetherId") Long togetherId,
            HttpServletRequest httpServletRequest
    ){
        return ResponseEntity.ok().body(togetherService.getTogether(togetherId, httpServletRequest));
    }

    @PutMapping("/together/{togetherId}")
    public ResponseEntity<Void> updateTogether(
            @PathVariable("togetherId") Long togetherId,
            @Valid @RequestPart(value = "data") TogetherRequestDTO.TogetherRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ){
        togetherService.updateTogether(togetherId, request, thumbnail);
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

}
