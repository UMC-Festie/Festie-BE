package com.umc.FestieBE.domain.calendar.application;

import com.umc.FestieBE.domain.calendar.dao.CalendarRepository;
import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.calendar.dto.CalendarRequestDTO;
import com.umc.FestieBE.domain.calendar.dto.CalendarResponseDTO;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.umc.FestieBE.global.exception.CustomErrorCode.CALENDAR_NOT_FOUND;
import static com.umc.FestieBE.global.exception.CustomErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final FestivalRepository festivalRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /** 캘린더 등록 */
    // 1. 연동 O -> 제목만 연동하고, 나머지는 사용자가 직접 입력 (= 날짜, 시간)
    // 2. 연동 X -> 사용자가 제목, 날짜, 시간 전부 직접 입력
    public void createCalendar(CalendarRequestDTO request) {
        // 유저
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Festival festival;
        Calendar calendar;

        // 공연/축제 정보 연동 시 DB 에서 확인
        if (request.getFestivalId() != null) {// 1. 연동 O
            festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            calendar = request.toEntity(user, festival);
            calendarRepository.save(calendar);
        } else { // 2. 연동 X
            calendar = request.toEntity(user);
            calendarRepository.save(calendar);
        }
    }

    /** 캘린더 삭제 */
    public void deleteCalendar(Long calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CustomException(CALENDAR_NOT_FOUND));
        calendarRepository.delete(calendar);
    }

    /** 캘린더 조회 */
    public CalendarResponseDTO getCalendar(Long calendarId){
        Calendar calendar = calendarRepository.findByIdWithUser(calendarId)
                .orElseThrow(() -> new CustomException(CALENDAR_NOT_FOUND));

        return new CalendarResponseDTO(calendar);
    }
}
