package com.umc.FestieBE.global.exception;

import lombok.Getter;


@Getter
public class CustomException extends RuntimeException {

    private final CustomErrorCode customErrorCode;
    private final String message;


    public CustomException(CustomErrorCode customErrorCode){
        this.customErrorCode = customErrorCode;
        this.message = customErrorCode.getMessage();
    }

    public CustomException(CustomErrorCode customErrorCode, String message){
        this.customErrorCode = customErrorCode;
        this.message = message;
    }
}
