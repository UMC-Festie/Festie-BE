package com.umc.FestieBE.domain.open_performance.api;

import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import com.umc.FestieBE.domain.open_performance.dto.PerformanceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenPerformanceController {
    private final OpenPerformanceService openPerformanceService;

    //업데이트
    @GetMapping("/base/update-daily-p")
    public ResponseEntity<String> updateDataDaily(){
        try {
            openPerformanceService.getAndSaveAllPerform();
            return new ResponseEntity<>("Data updated successfullly", HttpStatus.OK);
        }catch (ParseException e){
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //정보보기
    @GetMapping("/base/performance")
    public ResponseEntity<PerformanceResponseDTO.PerformanceListResponse> getPerformance(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "region",required = false) String region,
            @RequestParam(value = "duration", required = false) String duration,
            @RequestParam(value = "sortBy",defaultValue = "최신순") String sortBy)
    {
        return ResponseEntity.ok().body(openPerformanceService.getPerformance(page,category,region,duration,sortBy));
    }

}



