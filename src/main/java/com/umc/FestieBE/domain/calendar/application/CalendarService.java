package com.umc.FestieBE.domain.calendar.application;

import com.umc.FestieBE.domain.calendar.dao.CalendarRepository;
import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.calendar.dto.CalendarRequestDTO;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;

    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    // 캘린더 등록
    public void createCalendar(CalendarRequestDTO.CalendarRequest request){
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Calendar calendar;
        calendar = request.toEntity(tempUser);
        calendarRepository.save(calendar);
    }

    // 캘린더 삭제
    public void deleteCalendar(Long calendarId, TemporaryUser tempUser) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CALENDAR_NOT_FOUND));

        if(!calendar.getTemporaryUser().equals(tempUser)) {
            throw new CustomException(CustomErrorCode.CALENDAR_USER_MISMATCH);
        }

        calendarRepository.delete(calendar);
    }
}
