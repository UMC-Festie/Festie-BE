package com.umc.FestieBE.domain.mypage.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.mypage.dao.MypageRepository;
import com.umc.FestieBE.domain.mypage.domain.Mypage;
import com.umc.FestieBE.domain.mypage.dto.MypageResponseDTO;
import com.umc.FestieBE.domain.review.dao.ReviewRepository;
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final MypageRepository mypageRepository;

    /**
     * 마이페이지 조회
     */
    public MypageResponseDTO.MypageUserResponse getMypage(User user) {
        Mypage mypage = mypageRepository.findByUser(user)
                .orElseGet(() -> createMypage(user));

        return new MypageResponseDTO.MypageUserResponse(mypage);
    }

    private Mypage createMypage(User user) {
        String gender = user.getGender() == 'F' ? "여" : "남";

        /** 나이 -> 생일로 변경 */
        // LocalDate currentDate = LocalDate.now();
        // Period period = Period.between(user.getBirthday(), currentDate);
        // Integer age = period.getYears();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String birth = user.getBirthday().format(dateFormatter);

        Mypage mypage = Mypage.builder()
                .user(user)
                .nickname(user.getNickname())
                .email(user.getEmail())
                .gender(gender)
                .birth(birth)
                .build();

        return mypageRepository.save(mypage);
    }
}