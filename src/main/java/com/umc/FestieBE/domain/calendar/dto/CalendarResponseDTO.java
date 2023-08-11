package com.umc.FestieBE.domain.calendar.dto;

import com.umc.FestieBE.domain.calendar.domain.Calendar;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CalendarResponseDTO {
    private String calendarTitle;
    private String calendarDate;

    private int calendarYear;
    private int calendarMonth;
    private int calendarDay;
    private String calendarTime;

    private Boolean isWriter;

    public CalendarResponseDTO (Calendar calendar, Boolean isWriter) {
        // 캘린더 시간 형식 변경 -> "시간:분" 형식으로 변경
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String updateTime = calendar.getCalendarTime().format(formatter);

        // 캘린더 날짜 형식 변경 -> 년도, 월, 일로 분리
        LocalDate date = calendar.getCalendarDate();
        this.calendarYear = date.getYear();
        this.calendarMonth = date.getMonthValue();
        this.calendarDay = date.getDayOfMonth();

        this.calendarTitle = String.valueOf(calendar.getTitle());
        this.calendarDate = String.valueOf(calendar.getCalendarDate());
        this.calendarTime = updateTime;
        this.isWriter = isWriter;
    }
}
