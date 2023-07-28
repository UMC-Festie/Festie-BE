package com.umc.FestieBE.domain.openapi_2.application;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.openapi_2.dto.OpenDetailDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;


@Service
public class OpenService {

    // 서비스키 고정값 (변경 가능)
    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //oepnapi 호출
    RestTemplate restTemplate = new RestTemplate();

    public OpenService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getPerformanceDetail(String mt20id) {
        // OpenAPI 호출을 위한 URL 생성
        String Url = "http://www.kopis.or.kr/openApi/restful/pblprfr/";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Url)
                .path(mt20id)
                .queryParam("service",FIXED_API_KEY)
                .encode();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, entity, String.class
        );

        // XML 데이터를 자바 객체로 변환
        XmlMapper xmlMapper = new XmlMapper();
        OpenDetailDTO[] detailDTO;
        try {
            detailDTO = xmlMapper.readValue(response.getBody(), OpenDetailDTO[].class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        //json 반환하기
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(detailDTO);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
        return jsonResult;


//        return detailDTO;
    }

}
