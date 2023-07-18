package com.umc.FestieBE.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class ErrorResponse {

    private final int status;
    private final String errorCode;
    private final String message;

}
