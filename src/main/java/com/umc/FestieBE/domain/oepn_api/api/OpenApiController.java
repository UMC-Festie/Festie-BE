package com.umc.FestieBE.domain.oepn_api.api;


import com.umc.FestieBE.domain.oepn_api.application.OpenApiService;
import com.umc.FestieBE.domain.oepn_api.dto.OpenApiDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class OpenApiController {
    private final OpenApiService openApiService;

    public OpenApiController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    //defaultValue 설정하면 null일때 임의로 값을 넣어주는 역할을 한다.
    //공연정보보기
    @GetMapping("/base/performance-list")
    public ResponseEntity<Map<String, Object>> getPerform(
            @RequestParam("stdate") Integer startDate,
            @RequestParam("eddate") Integer endDate,
            @RequestParam("cpage") Integer currentpage,
            @RequestParam("rows") Integer rows,
            @RequestParam(value = "category",required = false) Integer category,
            @RequestParam(value = "region",required = false) String region,
            @RequestParam(value = "period",required = false) Integer period,
            @RequestParam(value = "sort",required = false) Integer sort
    ){
        //서비스를 통해 openapi 호출 및 데이터 반환
        OpenApiDTO openApiDTO = openApiService.getPerform(startDate,endDate,currentpage,rows,category,region,period,sort);

        //response body 구성
        Map<String , Object> responseBody = new HashMap<>();
        responseBody.put("isSuccess", true);
        responseBody.put("code",0);
        responseBody.put("message","string");
        Map<String, Object> result = new HashMap<>();
        result.put("numberOfElements",1);//예시로 1로 설정
        result.put("dto", Collections.singletonList(openApiDTO));
        responseBody.put("result",result);


        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


}

