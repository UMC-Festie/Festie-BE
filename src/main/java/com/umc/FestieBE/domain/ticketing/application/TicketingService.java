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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // final이나 @NotNull 필드의 생성자를 자동 생성
@Service
public class TicketingService {
    private final TicketingRepository ticketingRepository;
    private final FestivalRepository festivalRepository;
    // 임시 유저 (테스트용)
    private final TemporaryUserService temporaryUserService;

    // 티켓팅 글 생성
    public void createTicketing(TicketingRequestDTO.TicketingRequest request) {
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Ticketing ticketing;

        // 축제,공연 연동 X
        if(request.getFestivalId() == null) {
            ticketing = request.toEntity(tempUser);
        } else { // 축제,공연 연동 O
            Festival festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            ticketing = request.toEntity(tempUser, festival);
        }
        ticketingRepository.save(ticketing);
    }

    // 티켓팅 글 삭제
    public void deleteTicketing(Long ticketId, TemporaryUser tempUser) {
        Ticketing ticketing = ticketingRepository.findById(ticketId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.TICKETING_NOT_FOUND));

        if (!ticketing.getTemporaryUser().equals(tempUser)) {
            throw new CustomException((CustomErrorCode.TICKETING_USER_MISMATCH));
        }
        ticketingRepository.delete(ticketing);
    }
}
