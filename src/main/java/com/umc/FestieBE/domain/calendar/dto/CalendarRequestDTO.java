package com.umc.FestieBE.domain.calendar.dto;
import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import lombok.Getter;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class CalendarRequestDTO {
    @Getter
    public static class CalendarRequest {
        @NotBlank(message = "캘린더 제목은 필수 입력 값입니다.")
        private String calendarTitle;

        @NotBlank(message = "캘린더 날짜는 필수 입력 값입니다.")
        private LocalDate calendarDate;

        private Calendar.CalendarBuilder buildCommonProperties() {
            return Calendar.builder()
                    .title(calendarTitle)
                    .calendarDate(calendarDate);
        }

        public Calendar toEntity(TemporaryUser tempUser) {
            return buildCommonProperties()
                    .temporaryUser(tempUser)
                    .build();
        }
    }
}
