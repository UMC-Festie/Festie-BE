package com.umc.FestieBE.domain.applicant_info.dto;

import com.umc.FestieBE.domain.together.dto.BestieResponseDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicantInfoBestieListDTO {
    private long totalCount;
    private List<BestieResponseDTO> data;

    public ApplicantInfoBestieListDTO(List<BestieResponseDTO> data, long totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }
}
