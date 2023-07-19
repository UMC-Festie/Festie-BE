package com.umc.FestieBE.domain.calendar.api;

import com.umc.FestieBE.domain.calendar.application.CalendarService;
import com.umc.FestieBE.domain.calendar.dto.CalendarRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/Calendar")
@RequiredArgsConstructor
@RestController
public class CalendarController {
    private final CalendarService calendarService;

    @PostMapping("")
    public ResponseEntity<Void> createCalendar(@Valid @RequestBody CalendarRequestDTO.CalendarRequest request){
        calendarService.createCalendar(request);
        return ResponseEntity.ok().build();
    }
}
