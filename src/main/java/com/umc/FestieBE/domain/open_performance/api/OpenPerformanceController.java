package com.umc.FestieBE.domain.open_performance.api;

import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
public class OpenPerformanceController {
    private final OpenPerformanceService openPerformanceService;

    public OpenPerformanceController(OpenPerformanceService openPerformanceService){this.openPerformanceService = openPerformanceService;}


    @GetMapping("/open/performance-list")
    public ResponseEntity<OpenPerformanceDTO[]> getPerform(
            @RequestParam(value = "category",required = false) Integer category,
            @RequestParam(value = "region",required = false) String region,
            @RequestParam(value = "period",required = false) Integer period,
            @RequestParam(value = "sort",required = false) Integer sort) throws ParseException {
        //서비스를 통해 openapi 호출 및 데이터 반환
        OpenPerformanceDTO[] jsonResult = openPerformanceService.getPerform(category, region, period, sort);
        if (jsonResult == null) {
            // 데이터를 가져오지 못했을 경우에 대한 예외 처리 (이 부분 나중에 변경)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonResult, headers, HttpStatus.OK);
    }


}



