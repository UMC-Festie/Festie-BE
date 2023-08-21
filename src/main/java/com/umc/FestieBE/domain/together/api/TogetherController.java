package com.umc.FestieBE.domain.together.api;

import com.umc.FestieBE.domain.festival.dto.FestivalSearchResponseDTO;
import com.umc.FestieBE.domain.together.application.SearchService;
import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.together.dto.HomeResponseDTO;
import com.umc.FestieBE.domain.together.dto.SearchResponseDTO;
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
    private final SearchService searchService;


    // 같이가요 게시글 등록
    @PostMapping("/together")
    public ResponseEntity<Void> createTogether(
            @Valid @RequestPart(value = "data") TogetherRequestDTO.TogetherRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ){
        togetherService.createTogether(request, thumbnail);
        return ResponseEntity.ok().build();
    }

    // 같이가요 게시글 상세 조회
    @GetMapping("/together/{togetherId}")
    public ResponseEntity<TogetherResponseDTO.TogetherDetailResponse> getTogether(
            @PathVariable("togetherId") Long togetherId,
            HttpServletRequest httpServletRequest
    ){
        return ResponseEntity.ok().body(togetherService.getTogether(togetherId, httpServletRequest));
    }

    // 같이가요 게시글 수정
    @PutMapping("/together/{togetherId}")
    public ResponseEntity<Void> updateTogether(
            @PathVariable("togetherId") Long togetherId,
            @Valid @RequestPart(value = "data") TogetherRequestDTO.TogetherRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ){
        togetherService.updateTogether(togetherId, request, thumbnail);
        return ResponseEntity.ok().build();
    }

    // 같이가요 게시글 삭제
    @DeleteMapping("/together/{togetherId}")
    public ResponseEntity<Void> deleteTogether(
            @PathVariable("togetherId") Long togetherId
    ){
        togetherService.deleteTogether(togetherId);
        return ResponseEntity.ok().build();
    }

    // 같이가요 게시글 목록 조회
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

    // 티켓팅/후기/같이가요 - 공연/축제 연동 (검색)
    @GetMapping("/festival/search")
    public ResponseEntity<FestivalSearchResponseDTO.FestivalListResponse> getFestivalSearchList(
            @RequestParam(value = "keyword") String keyword
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalSearchList(keyword));
    }

    // 티켓팅/후기/같이가요 - 공연/축제 연동 (선택)
    @GetMapping("/festival/search/{boardType}/{festivalId}")
    public ResponseEntity<FestivalSearchResponseDTO.FestivalInfoResponse> getFestivalSelectedInfo(
            @PathVariable("boardType") String boardType,
            @PathVariable("festivalId") String festivalId
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalSelectedInfo(boardType, festivalId));
    }

    // 홈화면 - 곧 다가와요/같이가요
    @GetMapping("/home")
    public ResponseEntity<HomeResponseDTO> getFestivalAndTogetherList(
            @RequestParam(value = "festivalType", required = false) Integer festivalType,
            @RequestParam(value = "togetherType", required = false) Integer togetherType
    ){
        return ResponseEntity.ok().body(togetherService.getFestivalAndTogetherList(festivalType, togetherType));
    }

    // 통합 검색
    @GetMapping("/search")
    public ResponseEntity<SearchResponseDTO.SearchListResponse> searchAll(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "boardType", defaultValue = "전체") String boardType,
            @RequestParam(value = "sortBy", defaultValue = "최신순") String sort,
            @RequestParam(value = "page") Integer page
    ){
        return ResponseEntity.ok().body(searchService.getSearchResultList(keyword, boardType, sort, page));
    }

}
