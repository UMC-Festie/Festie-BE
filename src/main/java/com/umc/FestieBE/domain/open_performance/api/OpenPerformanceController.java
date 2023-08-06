package com.umc.FestieBE.domain.open_performance.api;

import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenPerformanceController {
    private final OpenPerformanceService openPerformanceService;

    public OpenPerformanceController(OpenPerformanceService openPerformanceService){this.openPerformanceService = openPerformanceService;}


    @GetMapping("/update-daily")
    public ResponseEntity<String> updateDataDaily(){
        try {
            openPerformanceService.updateDataDaily();
            return new ResponseEntity<>("Data updated successfullly", HttpStatus.OK);
        }catch (ParseException e){
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}



