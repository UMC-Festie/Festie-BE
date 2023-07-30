package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.oepn_api.dto.FestivalResponseDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class HomeResponseDTO {
    private List<FestivalResponseDTO.FestivalHomeListResponse> festivalList;
    private List<TogetherResponseDTO.TogetherHomeListResponse> togetherList;

    public HomeResponseDTO(
            List<FestivalResponseDTO.FestivalHomeListResponse> festivalList,
            List<TogetherResponseDTO.TogetherHomeListResponse> togetherList){
        this.festivalList = festivalList;
        this.togetherList = togetherList;
    }
}
