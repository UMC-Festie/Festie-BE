package com.umc.FestieBE.domain.together.application;

import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TogetherStatusScheduler {

    private final TogetherRepository togetherRepository;

    // 매일 자정에 실행하는 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyTask() {
        // status가 0(모집 중)이더라도 togetherDate가 지나면 1(모집 종료)로 바꾼다
        togetherRepository.updateStatusMatchedAutomatically(LocalDate.now());
    }

}
