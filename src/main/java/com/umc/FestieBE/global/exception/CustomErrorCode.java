package com.umc.FestieBE.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    //implements ErrorCode

    // TODO Custom ErrorCode 추가해 주세요
    // Common (1xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "서버 내부에 오류가 있습니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, 1002, "잘못된 입력값입니다."),

    // User (2xxx)

    // (View) Festival (3xxx)

    // (Share) Festival (4xxx)
    FESTIVAL_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "해당하는 공연/축제 정보가 없습니다.");

    // Ticketing (5xxx)

    // Review (6xxx)

    // Together (7xxx)


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}
