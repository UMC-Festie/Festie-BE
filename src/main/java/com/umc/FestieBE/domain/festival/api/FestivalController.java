package com.umc.FestieBE.domain.festival.api;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festival")
public class FestivalController {
    private final FestivalService festivalService;
    private final FestivalRepository festivalRepository;

    /** 새로운 공연/축제 등록 */
    @PostMapping("")
    public ResponseEntity<Void> createFestival(@RequestPart("request") FestivalRequestDTO request,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images, // 이미지는 필수 값 X
                                               @RequestPart("thumbnail") MultipartFile thumbnail) {
        if (images == null) { // 이미지 첨부 안하는 경우 처리
            images = Collections.emptyList();
        }
        festivalService.createFestival(request, images, thumbnail);
        return ResponseEntity.ok().build();
    }

    /** 새로운 공연/축제 수정 */
    @PutMapping("/{festivalId}")
    public ResponseEntity<Void> updateFestival(@PathVariable("festivalId") Long festivalId,
                                               @RequestPart FestivalRequestDTO request,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images, // 이미지는 필수 값 X
                                               @RequestPart("thumbnail") MultipartFile thumbnail) {
        if (images == null) { // 이미지 첨부 안하는 경우 처리
            images = Collections.emptyList();
        }
        festivalService.updateFestival(festivalId, request, images, thumbnail);
        return ResponseEntity.ok().build();
    }


    /** 새로운 공연/축제 삭제
     * -> isDeleted값만 true로 변경
     * */
    @PatchMapping("/{festivalId}")
    public ResponseEntity<Void> deleteFestival(@PathVariable("festivalId") Long festivalId,
                                               @RequestBody FestivalRequestDTO request) {
        Boolean isDeleted = request.getIsDeleted(); // 클라이언트로부터 전달된 isDeleted 값

        festivalService.deleteFestival(festivalId, isDeleted);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{festivalId}")
    public ResponseEntity<FestivalResponseDTO.FestivalDetailResponse> getFestival(
            @PathVariable("festivalId") Long festivalId,
            HttpServletRequest httpServletRequest)
    {
        return ResponseEntity.ok().body(festivalService.getFestival(festivalService, festivalId, httpServletRequest));
    }


    /** 새로운 축제,공연 목록조회 (무한 스크롤) */
    @GetMapping("")
    public ResponseEntity<FestivalResponseDTO.FestivalListResponse> getFestivalList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "sortBy", required = false, defaultValue = "최신순") String sortBy,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "duration", required = false) String duration) {
        return ResponseEntity.ok().body(festivalService.fetchFestivalPage(page, sortBy, category, region, duration));
    }
}