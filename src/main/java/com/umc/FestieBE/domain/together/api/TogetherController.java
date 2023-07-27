package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.print.Pageable;
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
    //public ResponseEntity<TogetherResponseDTO.TogetherListResponse> getTogetherList(
    public ResponseEntity<Object> getTogetherList( //Object
            @RequestParam(value = "page") int page,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "category", required = false) Integer category,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "status", required = false, defaultValue = "0") Integer status, // 모집 중
            @RequestParam(value = "sortBy", required = false, defaultValue = "0") Integer sort // 최신 순
    ){
        List<TogetherResponseDTO.TogetherListResponse> response = togetherService.getTogetherList(page, type, category, region, status, sort);
        //return ResponseEntity.ok().body();
        return ResponseEntity.ok().body(response);
    }

}
