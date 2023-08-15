package com.umc.FestieBE.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    //implements ErrorCode

    // TODO Custom ErrorCode 추가해 주세요
    // Common (1xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "서버 내부에 오류가 있습니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, 1002, "잘못된 입력값입니다."),
    KEYWORD_MISSING_ERROR(HttpStatus.BAD_REQUEST, 1003, "검색 키워드가 없습니다"),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 1003, "이미지 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 1004, "이미지 삭제에 실패했습니다."),
    PARAMETER_IS_MISSING(HttpStatus.BAD_REQUEST, 1005, "필수 query string 값이 누락되었습니다."),

    // User (2xxx)
    EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, 2001, "이미 존재하는 이메일입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 2002, "password와 checkPassword가 일치하지 않습니다."),
    //EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 2003, "가입되지 않은 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 2004, "잘못된 비밀번호입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2005, "사용자를 찾을 수 없습니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, 2006, "게시글 수정 및 삭제 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2007, "토큰이 만료되었습니다."),
    INVALID_TOKEN_FORMAT(HttpStatus.BAD_REQUEST, 2008, "잘못된 토큰 형식입니다."),
    TOKEN_VALIDATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2009, "토큰 검증 중 오류가 발생했습니다."),

    // (View) Festival (3xxx)

    // (Share) Festival (4xxx)
    FESTIVAL_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "해당하는 공연/축제 정보가 없습니다."),
    IMAGE_UPLOAD_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, 4002, "이미지는 최대 5장까지만 업로드 가능합니다."),

    // Ticketing (5xxx)
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, 5001, "존재하지 않는 캘린더 일정입니다."),
    CALENDAR_USER_MISMATCH(HttpStatus.FORBIDDEN, 5002, "캘린더 권한이 없는 유저입니다."),
    TICKETING_NOT_FOUND(HttpStatus.NOT_FOUND, 5003, "존재하지 않는 티켓팅 게시글입니다."),

    // Review (6xxx)
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, 6001, "존재하지 않는 후기 게시글입니다"),

    // Together (7xxx)
    TOGETHER_NOT_FOUND(HttpStatus.NOT_FOUND, 7001, "해당하는 같이가요 게시글이 없습니다."),
    APPLICANT_INFO_ALREADY_EXISTS(HttpStatus.CONFLICT, 7002, "이미 Bestie를 신청한 내역이 존재합니다."),
    MATCHING_ALREADY_COMPLETED(HttpStatus.CONFLICT, 7003, "Bestie 매칭이 종료된 게시글입니다."),
    SELF_APPLICATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, 7004, "자신이 작성한 게시글에는 Bestie 신청이 불가합니다."),
    BESTIE_SELECTION_NOT_ALLOWED(HttpStatus.FORBIDDEN, 7005, "자신이 작성하지 않은 게시글은 Bestie 선택이 불가합니다."),

    // Likes (8xxx)
    LIKES_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "좋아요/싫어요 할 게시글 타입 식별자는 필수 입력값입니다."),
    LIKES_ALREADY_EXISTS(HttpStatus.FORBIDDEN, 8002, "해당 게시글에 이미 좋아요/싫어요 한 내역이 있습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}