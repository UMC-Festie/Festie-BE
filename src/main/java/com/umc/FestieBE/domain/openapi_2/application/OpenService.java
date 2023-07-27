package com.umc.FestieBE.domain.openapi_2.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.FestieBE.domain.openapi_2.dto.OpenDetailDTO;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

public class OpenService {
    public String getPerformanceDerail(String mt20id) {
        //OpenAPI 호출을 위한 URL 생성
        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("mt20id")
                .queryParam("service", FIXED_API_KEY); // 서비스키를 고정값으로 추가

        //Json 형식의 응답을 기대하도록 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //XML변환을 위한 HttpMessageConverter 등록
        restTemplate.getMessageConverters().add(
                new MappingJackson2XmlHttpMessageConverter(new XmlMapper()));

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, entity, String.class
        );

        //xml mapping하기
        ObjectMapper xmlMapper = new XmlMapper();
        OpenDetailDTO[] detailDTOS;
        try {
            detailDTOS = xmlMapper.readValue(response.getBody(), OpenDetailDTO[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        //json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(detailDTOS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        return jsonResult;

    }

}
