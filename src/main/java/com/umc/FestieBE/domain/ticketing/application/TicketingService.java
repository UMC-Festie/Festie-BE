package com.umc.FestieBE.domain.ticketing.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.ticketing.dto.TicketingRequestDTO;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // final이나 @NotNull 필드의 생성자를 자동 생성
@Service
public class TicketingService {
    private final TicketingRepository ticketingRepository;
    private final FestivalRepository festivalRepository;
    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    // 티켓팅 게시글 등록
    public void createTicketing(TicketingRequestDTO request) {
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Festival festival;
        Ticketing ticketing;

        // 공연/축제 정보 연동 시 DB 에서 확인
        if(request.getFestivalId() != null) { // 1. 축제, 공연 연동 O
            festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            ticketing = request.toEntity(tempUser, festival);
            ticketingRepository.save(ticketing);
        } else { // 1. 축제, 공연 연동 X
            FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
            ticketing = request.toEntity(tempUser, festivalType);
            ticketingRepository.save(ticketing);
        }
    }

}
