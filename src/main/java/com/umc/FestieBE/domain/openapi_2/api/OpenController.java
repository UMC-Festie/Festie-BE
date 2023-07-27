package com.umc.FestieBE.domain.openapi_2.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class OpenController {
    //공연 상세보기
    @GetMapping("/base/")
    public ResponseEntity<String> getPerformanceDetail(
            @PathVariable("mt20id") String mt20id){

        String detailDTO = openService.getPerformanceDerail(mt20id);
        if (detailDTO != null){
            return new ResponseEntity<>(detailDTO, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

}
