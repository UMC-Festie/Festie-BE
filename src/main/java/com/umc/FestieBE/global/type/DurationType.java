package com.umc.FestieBE.global.type;

import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;


@Getter
@RequiredArgsConstructor
public enum DurationType {

    WILL("공연예정", "축제예정", 0),
    ING("공연중", "축제중",1),
    END("공연완료", "축제완료", 2);



    private final String duration;
    private final String state;
    private final int value;

    private static DurationType findDurationType(Integer duration){
        return Arrays.stream(DurationType.values())
                .filter(d -> d.getValue() == duration)
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE, "해당되는 기간이 없습니다."));
    }

    public static DurationType findDurationType(String duration){
        return Arrays.stream(DurationType.values())
                .filter(d -> d.getDuration().equals(duration))
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE,"해당되는 기간이 없습니다." ));
    }
    public static DurationType findStateType (String state){
        return Arrays.stream(DurationType.values())
                .filter(d -> d.getState().equals(state))
                .findFirst()
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VALUE,"해당되는 기간이 없습니다." ));
    }


}
