package com.umc.FestieBE.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Custom Exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("*** Custom Exception - url: {}, errorCode: {}, errorMessage: {}",
                request.getRequestURL(), e.getCustomErrorCode(), e.getMessage());
        return handleExceptionInternal(e.getCustomErrorCode());
    }

    // @Valid Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request){
        log.error("*** Validation Exception - url: {}, errorMessage: {}",
                request.getRequestURL(), e.getMessage());

        // 유효성 검증 실패한 필드 리스트
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

        StringBuilder errorMessageBuilder = new StringBuilder();
        fieldErrorList.forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errorMessageBuilder
                    .append(field)
                    .append(" : ")
                    .append(message)
                    .append("; ");
        });
        String errorMessage = errorMessageBuilder.toString();

        return handleExceptionInternal(CustomErrorCode.INVALID_VALUE, errorMessage);
    }

    // 기타 Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception e, HttpServletRequest request) {
        log.error("*** Common Exception - url: {}, errorMessage: {}",
                request.getRequestURL(), e.getMessage());
        return handleExceptionInternal(CustomErrorCode.INTERNAL_SERVER_ERROR);
    }


    // CustomErrorCode 를 기반으로 ResponseEntity 생성
    private ResponseEntity<Object> handleExceptionInternal(CustomErrorCode customErrorCode) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(customErrorCode.getHttpStatus().value())
                .errorCode(customErrorCode.name())
                .message(customErrorCode.getMessage())
                .build();

        return ResponseEntity
                .status(customErrorCode.getHttpStatus())
                .body(errorResponse);
    }

    private ResponseEntity<Object> handleExceptionInternal(CustomErrorCode customErrorCode,
                                                           String errorMessage) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(customErrorCode.getHttpStatus().value())
                .errorCode(customErrorCode.name())
                .message(errorMessage)
                .build();

        return ResponseEntity
                .status(customErrorCode.getHttpStatus())
                .body(errorResponse);
    }

}
