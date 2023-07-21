package com.umc.FestieBE.domain.festival.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FestivalService {
    private final FestivalRepository festivalRepository;

    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    // 새로운 공연,축제 등록
    public void createFestival(FestivalRequestDTO.FestivalRequest request){
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();
        Festival festival = request.toEntity(tempUser);

        festivalRepository.save(festival);
    }
}
