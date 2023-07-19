package com.umc.FestieBE.domain.oepn_api.api;


import com.umc.FestieBE.domain.oepn_api.application.OpenApiService;
import com.umc.FestieBE.domain.oepn_api.dto.OpenApiDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OpenApiController {
    private final OpenApiService openApiService;

    public OpenApiController(OpenApiService openApiService){
        this.openApiService = openApiService;
    }

    //defaultValue 설정하면 null일때 임의로 값을 넣어주는 역할을 한다.
    //공연정보보기
    @GetMapping("/base/performance-list")
    public List<OpenApiDTO> getPerform(
            @RequestParam("stdate") Integer startDate,
            @RequestParam("eddate") Integer endDate,
            @RequestParam("cpage") Integer currentpage,
            @RequestParam("rows") Integer rows,
            @RequestParam(value = "category",required = false) Integer category,
            @RequestParam(value = "region",required = false) String region,
            @RequestParam(value = "period",required = false) Integer period,
            @RequestParam(value = "sort",required = false) Integer sort
    ){
        List<OpenApiDTO> performDtos = openApiService.getPerform(startDate,endDate,currentpage,rows,category,region,period,sort);
        return performDtos;
    }


}

