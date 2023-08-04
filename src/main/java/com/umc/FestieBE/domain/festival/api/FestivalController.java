package com.umc.FestieBE.domain.festival.api;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalPaginationResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.RegionType;
import com.umc.FestieBE.global.type.SortedType;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.SortedMapType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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
        festivalService.createFestival(request, images, thumbnail);
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
    public ResponseEntity<FestivalResponseDTO> getFestival(@PathVariable("festivalId") Long festivalId){
        return ResponseEntity.ok().body(festivalService.getFestival(festivalService, festivalId));
    }


    /** 새로운 축제,공연 목록조회 (무한 스크롤) */
    @GetMapping("")
    public List<FestivalPaginationResponseDTO> getFestivalList(
            @RequestParam(required = false, defaultValue = "LATEST") String sortBy,
            @RequestParam(required = false) CategoryType category,
            @RequestParam(required = false) RegionType region,
            @RequestParam(required = false) String duration) {
        return festivalService.fetchFestivalPage(sortBy, category, region, duration);
    }
}
