package com.umc.FestieBE.domain.together.dto;

import com.umc.FestieBE.domain.open_festival.dto.FestivalListResponseDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class HomeResponseDTO {
    private List<FestivalListResponseDTO.FestivalHomeListResponse> festivalList;
    private List<TogetherResponseDTO.TogetherHomeListResponse> togetherList;

    public HomeResponseDTO(
            List<FestivalListResponseDTO.FestivalHomeListResponse> festivalList,
            List<TogetherResponseDTO.TogetherHomeListResponse> togetherList){
        this.festivalList = festivalList;
        this.togetherList = togetherList;
    }
}
