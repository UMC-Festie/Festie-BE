package com.umc.FestieBE.domain.open_festival.api;

import com.umc.FestieBE.domain.open_festival.application.OpenFestivalService;
import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenFestivalController {
    private final OpenFestivalService openFestivalService;

    public OpenFestivalController(OpenFestivalService openFestivalService){this.openFestivalService = openFestivalService;}


    @GetMapping("/base/update-daily-f")
    public ResponseEntity<String> updateDataDaily(){
        try {
            openFestivalService.updateDataDaily();
            return new ResponseEntity<>("Data updated successfullly", HttpStatus.OK);
        }catch (ParseException e){
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
