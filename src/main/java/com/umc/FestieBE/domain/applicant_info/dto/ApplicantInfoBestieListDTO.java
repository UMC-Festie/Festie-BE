package com.umc.FestieBE.domain.applicant_info.dto;

import com.umc.FestieBE.domain.together.dto.BestieResponseDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicantInfoBestieListDTO {
    private Long totalCount;
    private Integer pageNum;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private List<BestieResponseDTO> data;

    public ApplicantInfoBestieListDTO(List<BestieResponseDTO> data,
                              Long totalCount,
                              Integer pageNum,
                              Boolean hasNext,
                              Boolean hasPrevious) {
        this.totalCount = totalCount;
        this.pageNum = pageNum;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.data = data;
    }
}
