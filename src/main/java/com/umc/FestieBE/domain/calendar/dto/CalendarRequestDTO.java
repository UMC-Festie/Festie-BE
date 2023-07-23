package com.umc.FestieBE.domain.calendar.dto;
import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import lombok.Getter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CalendarRequestDTO {
    private Long festivalId;

    @NotBlank(message = "캘린더 제목은 필수 입력 값입니다.")
    private String calendarTitle;

    @NotNull(message = "캘린더 날짜는 필수 입력 값입니다.")
    private LocalDate calendarDate;

    @NotNull(message = "캘린더 시간은 필수 입력 값입니다.")
    private LocalTime calendarTime;

    // 1. 축제, 공연 연동 O (festivalId != null인 경우)
    public Calendar toEntity(TemporaryUser tempUser, Festival festival) {
        return Calendar.builder()
                .temporaryUser(tempUser)
                .festivalId(festivalId)
                .title(festival.getFestivalTitle())
                .calendarDate(calendarDate)
                .calendarTime(calendarTime)
                .build();
    }

    // 2. 축제, 공연 연동 X (festivalId == null인 경우)
    public Calendar toEntity(TemporaryUser tempUser) {
        return Calendar.builder()
                .temporaryUser(tempUser)
                .festivalId(festivalId)
                .title(calendarTitle)
                .calendarDate(calendarDate)
                .calendarTime(calendarTime)
                .build();
    }
}
