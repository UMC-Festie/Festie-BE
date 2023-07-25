package com.umc.FestieBE.domain.festival.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FestivalService {
    private final FestivalRepository festivalRepository;
    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    // [새로운 공연,축제 등록]
    public void createFestival(FestivalRequestDTO request){
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        RegionType regionType = RegionType.findRegionType(request.getFestivalRegion());
        Boolean isDeleted = false;

        Festival festival = request.toEntity(tempUser, festivalType, regionType, isDeleted);
        festivalRepository.save(festival);
    }

    // [새로운 공연, 축제 수정]
    public void updateFestival(Long festivalId, FestivalRequestDTO request) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));

        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        RegionType regionType = RegionType.findRegionType(request.getFestivalRegion());
        Boolean isDeleted = false;

        festival.updateAndDeleteFestival(festival.getId(),
                request.getFestivalTitle(),
                festivalType,
                request.getThumbnailUrl(),
                request.getFestivalCategory(),
                regionType,
                request.getFestivalLocation(),
                request.getFestivalStartDate(),
                request.getFestivalEndDate(),
                request.getFestivalStartTime(),
                request.getPostTitle(),
                request.getContent(),
                request.getFestivalAdminsName(),
                request.getFestivalAdminsPhone(),
                request.getFestivalAdminsSiteAddress(),
                isDeleted
        );
        festivalRepository.save(festival);
    }

    // [새로운 공연, 축제 삭제]
    // * 새로운 공연, 축제 삭제 시 해당 데이터가 진짜 삭제 되면 안됨!
    // : 데이터 삭제 안하고 isDeleted값이 true가 되도록 함
    public void deleteFestival(Long festivalId, FestivalRequestDTO request) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));

        Boolean isDeleted = true; // isDeleted로 삭제 여부 표기
        festival.updateAndDeleteFestival(festival.getId(),
                festival.getFestivalTitle(),
                festival.getType(),
                festival.getThumbnailUrl(),
                festival.getCategory(),
                festival.getRegion(),
                festival.getLocation(),
                festival.getStartDate(),
                festival.getEndDate(),
                festival.getStartTime(),
                festival.getTitle(),
                festival.getContent(),
                festival.getAdminsName(),
                festival.getAdminsPhone(),
                festival.getAdminsSiteAddress(),
                isDeleted
                );

        festivalRepository.save(festival);
    }
}
