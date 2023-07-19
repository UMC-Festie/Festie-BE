package com.umc.FestieBE.domain.oepn_api.application;

import com.umc.FestieBE.domain.oepn_api.dto.OpenApiDTO;
import com.umc.FestieBE.domain.oepn_api.response.OpenApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenApiService {

    private final RestTemplate restTemplate;

    public OpenApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<OpenApiDTO> getPerform(Integer startDate, Integer endDate, Integer currentpage, Integer rows, Integer category, String region, Integer period, Integer sort ){
        //OpenAPI 호출을 위한 URL 생성
        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr?" +
                "service=e7280f000b59428793167d4b36222d7b" +
                "&stdate=" + startDate +
                "&eddate=" + endDate +
                "&cpage=" + currentpage +
                "&rows=" + rows  +
                (category != null ? "&category=" + category : "") +
                (region != null ? "&region=" + region : "") +
                (period != null ? "&period=" + period : "");

        //OpenAPI 호출
        OpenApiResponse response = restTemplate.getForObject(apiUrl, OpenApiResponse.class);

        //DTO로 변환하여 반환
        List<OpenApiDTO> openApiDTOS = convertToOpenApiDtoList(response);
        return openApiDTOS;
    }

    private List<OpenApiDTO> convertToOpenApiDtoList(OpenApiResponse response){
        List<OpenApiDTO> openApiDTOList = new ArrayList<>();

        //DataResponse에서 필요한 정보를 Dto로 변환하는 로직 구현
        if (response !=null && response.getResult() !=null && response.getResult().getDto() !=null){
            for (OpenApiDTO openApiDTO : response.getResult().getDto()){
                openApiDTOList.add(openApiDTO);
            }

        }
        return openApiDTOList;
    }
}
