package com.umc.FestieBE.domain.oepn_api.application;

import com.umc.FestieBE.domain.oepn_api.dto.OpenApiDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


//비즈니스 로직을 담당하는 계층, controller와 repository사이에서 데이터 처리를 담당.
//controller에서 받은 요청을 처리하고, 필요한 데이터를 dto로 변환하여 반환하는 역할.
@Service
public class OpenApiService {

    private final RestTemplate restTemplate;

    public OpenApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<OpenApiDTO.Result.Dto> getPerform(Integer startDate, Integer endDate, Integer currentpage, Integer rows, Integer category, String region, Integer period, Integer sort) {
        //OpenAPI 호출을 위한 URL 생성
        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr" +
                "?service=e7280f000b59428793167d4b36222d7b" +
                "&stdate=" + startDate +
                "&eddate=" + endDate +
                "&cpage=" + currentpage +
                "&rows=" + rows +
                (category != null ? "&category=" + category : "") +
                (region != null ? "&region=" + region : "") +
                (period != null ? "&period=" + period : "") +
                (period != null ? "&period=" + sort : "");

        //OpenAPI 호출
        OpenApiDTO response = restTemplate.getForObject(apiUrl, OpenApiDTO.class);

        //DTO 리스트 반환
        List<OpenApiDTO.Result.Dto> dtoList = new ArrayList<>();

        if (response != null && response.getResult() != null && response.getResult().size() > 0) {
            for (OpenApiDTO.Result result : response.getResult()) {
                if (result.getDto() != null) {
                    dtoList.addAll(result.getDto());
                }
            }
        }
        return dtoList;
    }



}
