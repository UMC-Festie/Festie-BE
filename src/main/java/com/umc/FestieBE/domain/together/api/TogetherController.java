package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalSearchResponseDTO;
import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.HomeResponseDTO;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;


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
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "status", required = false, defaultValue = "모집중") String status, // default: 모집 중
            @RequestParam(value = "sortBy", required = false, defaultValue = "최신순") String sort // default: 최신 순
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
            @PathVariable("festivalId") String festivalId
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalSelectedInfo(festivalId));
    }

    @GetMapping("home")
    public ResponseEntity<HomeResponseDTO> getFestivalAndTogetherList(
            @RequestParam(value = "festivalType", required = false) Integer festivalType,
            @RequestParam(value = "togetherType", required = false) Integer togetherType
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalAndTogetherList(festivalType, togetherType));
    }

}
