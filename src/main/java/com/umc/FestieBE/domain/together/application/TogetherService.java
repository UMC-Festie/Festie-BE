package com.umc.FestieBE.domain.together.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TogetherService {

    private final TogetherRepository togetherRepository;
    private final FestivalRepository festivalRepository;

    private final TemporaryUserService temporaryUserService;

    public void createTogether(TogetherRequestDTO request) {
        // 임시 유저
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Together together;
        // festival 직접 입력할 경우
        if(request.getFestivalId() == null){
            FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
            RegionType region = RegionType.findRegionType(request.getRegion());
            //카테고리
            together = request.toEntity(tempUser, festivalType, region);
        // festival 정보 연동할 경우
        }else{
            Festival festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            together = request.toEntity(tempUser, festival);
        }
        togetherRepository.save(together);

    }

}
