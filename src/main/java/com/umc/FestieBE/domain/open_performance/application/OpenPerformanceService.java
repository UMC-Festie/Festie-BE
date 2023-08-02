package com.umc.FestieBE.domain.open_performance.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Service
public class OpenPerformanceService {
    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //OpenAPI 호출
    RestTemplate restTemplate = new RestTemplate();
    public OpenPerformanceDTO[] getPerform(Integer category, String region, Integer period, Integer sort) throws ParseException {
        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                .queryParam("stdate", "20230701")
                .queryParam("eddate", "20230830")
                .queryParam("cpage", "1")
                .queryParam("rows", "8");

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

        //xml 매핑하기
        XmlMapper xmlMapper = new XmlMapper();
        OpenPerformanceDTO[] dtos;
        try {
            dtos = xmlMapper.readValue(response.getBody(), OpenPerformanceDTO[].class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }

        return dtos;

    }


}
