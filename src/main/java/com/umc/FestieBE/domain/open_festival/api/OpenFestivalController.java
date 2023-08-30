package com.umc.FestieBE.domain.open_festival.api;

import com.umc.FestieBE.domain.open_festival.application.OpenFestivalService;
import com.umc.FestieBE.domain.open_festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenFestivalController {
    private final OpenFestivalService openFestivalService;


    //축제 업데이트
    @GetMapping("/base/update-daily-f")
    public ResponseEntity<String> updateDataDaily() {
        try {
            openFestivalService.updateDataDaily();
            return new ResponseEntity<>("Data updated successfullly", HttpStatus.OK);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //축제 정보보기
    @GetMapping("/base/festival")
    public ResponseEntity<FestivalResponseDTO.FestivalListResponse> getFestival(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "duration", required = false) String duration,
            @RequestParam(value = "sortBy", defaultValue = "최신순") String sortBy) {
        return ResponseEntity.ok().body(openFestivalService.getFestival(page, category, region, duration, sortBy));
    }

    //축제 정보 상세보기
    @GetMapping("/base/{festivalId}")
    public ResponseEntity<String> getFestivalDetail(
            @PathVariable("festivalId") String festivalId,
            @RequestParam(value = "userId") Long userId
    ){
        String detailDTO = openFestivalService.getFestivalDetail(festivalId,userId);
        if (detailDTO == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(detailDTO, headers, HttpStatus.OK);
    }

    //좋아요 업데이트
    @GetMapping("/festival/update-daily-l")
    public ResponseEntity<String> updateLike(){
        openFestivalService.updateLikeCount();
        return new ResponseEntity<>("Festival like update successfully", HttpStatus.OK);
    }


    //조회수 업데이트
    @GetMapping("/festival/update-daily-v")
    public ResponseEntity<String> updateView(){
        openFestivalService.updateViewCount();
        return new ResponseEntity<>("Festival view update successfully", HttpStatus.OK);
    }
}
