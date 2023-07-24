package com.umc.FestieBE.domain.calendar.api;

import com.umc.FestieBE.domain.calendar.application.CalendarService;
import com.umc.FestieBE.domain.calendar.dto.CalendarRequestDTO;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/calendar")
@RequiredArgsConstructor
@RestController
public class CalendarController {
    private final CalendarService calendarService;
    private final TemporaryUserService temporaryUserService;

    // 캘린더 등록
    @PostMapping("")
    public ResponseEntity<Void> createCalendar(@Valid @RequestBody CalendarRequestDTO request){
        calendarService.createCalendar(request);
        return ResponseEntity.ok().build();
    }

    // 캘린더 삭제
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable Long calendarId) {
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok().build();
    }

}
