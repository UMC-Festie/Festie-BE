package com.umc.FestieBE.domain.openapi_2.api;

import com.umc.FestieBE.domain.openapi_2.application.OpenService;
import com.umc.FestieBE.domain.openapi_2.dto.OpenDetailDTO;
import com.umc.FestieBE.domain.openapi_2.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OpenController {
    private final OpenService openService;

    public OpenController(OpenService openService){
        this.openService = openService;
    }
    //공연 상세보기
    @GetMapping("/base/{mt20id}")
    public ResponseEntity<String> getPerformanceDetail(
            @PathVariable("mt20id") String mt20id){

        String detailDTO = openService.getPerformanceDetail(mt20id);
        if (detailDTO == null ) {
            // 데이터를 가져오지 못했을 경우에 대한 예외 처리 (이 부분 나중에 변경)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(detailDTO, HttpStatus.OK);
        }
    }


}
