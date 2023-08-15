package com.umc.FestieBE.domain.festival.dto;

import com.umc.FestieBE.domain.calendar.domain.Calendar;
import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.image.AwsS3Service;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.umc.FestieBE.global.exception.CustomErrorCode.FESTIVAL_NOT_FOUND;
import static com.umc.FestieBE.global.type.FestivalType.FESTIVAL;
import static com.umc.FestieBE.global.type.FestivalType.PERFORMANCE;

@Getter
// 새로운 공연, 축제 (api 연동 X)
public class FestivalRequestDTO {
    @NotBlank(message = "공연/축제 제목은 필수 입력 값입니다.")
    private String festivalTitle;

    @NotNull (message = "공연/축제 유형은 필수 입력 값입니다.")
    @Min(value = 0, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
    @Max(value = 1, message = "공연/축제 유형은 0(공연) 또는 1(축제)만 가능합니다.")
    private Integer festivalType; // 공연 or 축제

    @NotBlank(message = "공연/축제 썸네일 이미지 url은 필수 입력 값입니다.")
    private String thumbnailUrl;

    @NotNull(message = "공연/축제 카테고리는 필수 입력 값입니다.")
    private String category;

    @NotNull(message = "공연/축제 지역은 필수 입력 값입니다.")
    private String festivalRegion; // 공연,축제 지역

    @NotBlank(message = "공연/축제 상세 위치는 필수 입력 값입니다.")
    private String festivalLocation; // 공연,축제 상세 위치

    @NotNull(message = "공연/축제 시작일은 필수 입력 값입니다.")
    private LocalDate festivalStartDate;

    @NotNull(message = "공연/축제 종료일은 필수 입력 값입니다.")
    private LocalDate festivalEndDate;

    @NotNull(message = "공연/축제 시작 시간은 필수 입력 값입니다.")
    private LocalTime festivalStartTime;

    private String festivalAdminsName;
    private String festivalAdminsPhone;
    private String festivalAdminsSiteAddress;

    private String reservationLink; // 예매링크
    private String postTitle;
    private String content;

    @NotNull(message = "공연/축제 게시글의 삭제 여부는 필수 입력 값입니다.")
    private Boolean isDeleted;

    private String duration;

    private List<String> imagesUrl;

    public String calculateDuration(FestivalType festivalType, LocalDate festivalStartDate, LocalDate festivalEndDate){
        LocalDate currentDate = LocalDate.now(); // 유저 로컬 날짜

        String type = festivalType.getType();
        String duration = "";

        if (PERFORMANCE == festivalType || FESTIVAL == festivalType) {
            if (currentDate.isBefore(festivalStartDate)) {
                duration = type + "예정";
            } else if (currentDate.isAfter(festivalEndDate)) {
                duration = type + "종료";
            } else {
                duration = type + "중";
            }
        }
        return duration;
    }

    public Festival toEntity(User user,
                             FestivalType festivalType,
                             RegionType festivalRegion,
                             CategoryType category,
                             Boolean isDeleted,
                             List<String> imagesUrl,
                             String thumbnailUrl) {
        duration = calculateDuration(festivalType, festivalStartDate, festivalEndDate);

        return Festival.builder()
                .user(user)
                .view(0L)
                .likes(0L)
                .dislikes(0L)
                .festivalTitle(festivalTitle)
                .type(festivalType)
                .thumbnailUrl(thumbnailUrl)
                .category(category)
                .region(festivalRegion)
                .location(festivalLocation)
                .startDate(festivalStartDate)
                .endDate(festivalEndDate)
                .startTime(festivalStartTime)
                .adminsName(festivalAdminsName)
                .adminsPhone(festivalAdminsPhone)
                .adminsSiteAddress(festivalAdminsSiteAddress)
                .reservationLink(reservationLink)
                .title(postTitle)
                .content(content)
                .isDeleted(isDeleted)
                .duration(duration)
                .imagesUrl(imagesUrl)
                .build();
    }
}
