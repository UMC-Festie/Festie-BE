package com.umc.FestieBE.domain.openapi_2.application;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.openapi_2.dto.OpenDetailDTO;
import com.umc.FestieBE.domain.openapi_2.dto.ResponseDTO;
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
                .queryParam("service", FIXED_API_KEY)
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //json parsing
        ResponseDTO responseDTO = new ResponseDTO();

        OpenDetailDTO dto = detailDTO[0];
        String id = dto.getMt20id();
        String name = dto.getPrfnm();
        String profile = dto.getPoster();
        String startdate = dto.getPrfpdfrom();
        String enddate = dto.getPrfpdto();
        String datetime = dto.getDtguidance();
        String runtime = dto.getPrfruntime();
        String location = dto.getFcltynm();
//            String information = detail.getEntrpsnm();
        String details = dto.getSty();
        String images = dto.getStyurls().toString();
        String management = dto.getEntrpsnm();
        String price = dto.getPcseguidance();

        responseDTO.setId(id);
        responseDTO.setName(name);
        responseDTO.setProfile(profile);
        responseDTO.setStartDate(startdate);
        responseDTO.setEndDate(enddate);
        responseDTO.setDateTime(datetime);
        responseDTO.setRuntime(runtime);
        responseDTO.setLocation(location);
        responseDTO.setDetails(details);
        responseDTO.setImages(images);
        responseDTO.setManagement(management);
        responseDTO.setPrice(price);

        //json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(responseDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return jsonResult;
//        return responseDTO;

    }
}
