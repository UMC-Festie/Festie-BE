package com.umc.FestieBE.domain.together.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.TogetherDTO;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TogetherService {

    private final TogetherRepository togetherRepository;
    private final FestivalRepository festivalRepository;


    public void createTogether(TogetherDTO.TogetherRequest request) {
        // User user

        Together together;
        // festival 직접 입력할 경우
        if(request.getFestivalId() == null){
            FestivalType festivalType = findFestivalType(request.getFestivalType());
            RegionType region = findRegionType(request.getRegion());
            //카테고리
            together = request.toEntity(festivalType, region);
        // festival 정보 연동할 경우
        }else{
            Festival festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 축제 정보가 없습니다."));
            together = request.toEntity(festival);
        }
        togetherRepository.save(together);

    }

    private FestivalType findFestivalType(Integer festivalType){
        return Arrays.stream(FestivalType.values())
                .filter(f -> f.getValue() == festivalType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 공연/축제 유형이 없습니다."));
    }

    private RegionType findRegionType(String region){
        return Arrays.stream(RegionType.values())
                .filter(r -> r.getRegion().equals(region))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지역이 없습니다."));
    }

}
