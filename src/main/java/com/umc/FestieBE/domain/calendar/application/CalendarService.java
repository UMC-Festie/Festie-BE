package com.umc.FestieBE.domain.calendar.application;

import com.umc.FestieBE.domain.calendar.dao.CalendarRepository;
import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.calendar.dto.CalendarRequestDTO;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
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
    private final FestivalRepository festivalRepository;

    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    // [캘린더 등록]
    // 1. 연동 O -> 제목만 연동하고, 나머지는 사용자가 직접 입력 (= 날짜, 시간)
    // 2. 연동 X -> 사용자가 제목, 날짜, 시간 전부 직접 입력
    public void createCalendar(CalendarRequestDTO request) {
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Festival festival;
        Calendar calendar;

        // 공연/축제 정보 연동 시 DB 에서 확인
        if (request.getFestivalId() != null) {// 1. 연동 O
            festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            calendar = request.toEntity(tempUser, festival);
            calendarRepository.save(calendar);
        } else { // 2. 연동 X
            calendar = request.toEntity(tempUser);
            calendarRepository.save(calendar);
        }
    }

    // [캘린더 삭제]
    public void deleteCalendar(Long calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CALENDAR_NOT_FOUND));
        calendarRepository.delete(calendar);
    }
}
