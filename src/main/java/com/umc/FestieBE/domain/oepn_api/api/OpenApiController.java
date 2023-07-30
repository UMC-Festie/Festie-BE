package com.umc.FestieBE.domain.oepn_api.api;


import com.umc.FestieBE.domain.oepn_api.application.OpenApiService;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenApiController {
    private final OpenApiService openApiService;

    public OpenApiController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    //defaultValue 설정하면 null일때 임의로 값을 넣어주는 역할을 한다.
    //공연정보보기
    @GetMapping("/base/performance-list")
    public ResponseEntity<String> getPerform(
            @RequestParam("stdate") Integer startDate,
            @RequestParam("eddate") Integer endDate,
            @RequestParam("cpage") Integer currentpage,
            @RequestParam("rows") Integer rows,
            @RequestParam(value = "category",required = false) Integer category,
            @RequestParam(value = "region",required = false) String region,
            @RequestParam(value = "period",required = false) Integer period,
            @RequestParam(value = "sort",required = false) Integer sort
    ) throws ParseException {
        //서비스를 통해 openapi 호출 및 데이터 반환
        String jsonResult = openApiService.getPerform(startDate, endDate, currentpage, rows, category, region, period, sort);
        if (jsonResult == null) {
            // 데이터를 가져오지 못했을 경우에 대한 예외 처리 (이 부분 나중에 변경)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonResult, headers, HttpStatus.OK);
    }
    //축제 정보보기
    @GetMapping("/base/festival-list")
    public ResponseEntity<String> getFestie(
            @RequestParam("startdate") Integer startDate,
            @RequestParam("enddate") Integer endDate,
            @RequestParam("page") Integer currentpage,
            @RequestParam("rows") Integer rows,
            @RequestParam(value = "category",required = false) Integer category,
            @RequestParam(value = "region",required = false) String region,
            @RequestParam(value = "period",required = false) Integer period,
            @RequestParam(value = "sort",required = false) Integer sort
    ) throws ParseException {
        //서비스를 통해 openapi 호출 및 데이터 반환
        String jsonResult = openApiService.getFestie(startDate, endDate, currentpage, rows, category, region, period, sort);
        if (jsonResult == null) {
            // 데이터를 가져오지 못했을 경우에 대한 예외 처리 (이 부분 나중에 변경)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonResult, headers, HttpStatus.OK);
    }



}

