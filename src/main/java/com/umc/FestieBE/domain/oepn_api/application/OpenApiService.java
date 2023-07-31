package com.umc.FestieBE.domain.oepn_api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.oepn_api.dto.*;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;


//비즈니스 로직을 담당하는 계층, controller와 repository사이에서 데이터 처리를 담당.
//controller에서 받은 요청을 처리하고, 필요한 데이터를 dto로 변환하여 반환하는 역할.
@Service
public class OpenApiService {
    // 서비스키 고정값 (변경 가능)
    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //OpenAPI 호출
    RestTemplate restTemplate = new RestTemplate();

    public String getPerform(Integer startDate, Integer endDate, Integer currentpage, Integer rows, Integer category, String region, Integer period, Integer sort) throws ParseException {
        //OpenAPI 호출을 위한 URL 생성
        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                .queryParam("stdate", startDate)
                .queryParam("eddate", endDate)
                .queryParam("cpage", currentpage)
                .queryParam("rows", rows);

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
        PerformApiDTO[] events;
        try {
            events = xmlMapper.readValue(response.getBody(), PerformApiDTO[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        OpenApiDTO[] openApiDTOArray = new OpenApiDTO[events.length];
        OpenApiDTO openApiDTO = new OpenApiDTO();
            // events 배열 크기만큼 for문으로 각 객체의 정보를 가져와서 설정
        for (int i =0; i< events.length; i++) {
            PerformApiDTO event = events[i];

            String id = event.getMt20id();
            String name = event.getPrfnm();
            String profile = event.getPoster();
            String location = event.getFcltynm();
            String startDateStr = event.getPrfpdfrom();
            String endDateStr = event.getPrfpdto();
            String state = event.getPrfstate();
            String genrenm = event.getGenrenm();

            openApiDTO.setId(id);
            openApiDTO.setName(name);
            openApiDTO.setState(state);
            openApiDTO.setLocation(location);
            openApiDTO.setStartDate(startDateStr);
            openApiDTO.setEndDate(endDateStr);
            openApiDTO.setProfile(profile);
            openApiDTO.setGenrenm(genrenm);

            openApiDTOArray[i] = openApiDTO;
        }

        //json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(openApiDTOArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return jsonResult;

    }
    //공연 상세보기 + 축제 상세보기
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
        OpenApiDetailDTO[] detailDTO;
        try {
            detailDTO = xmlMapper.readValue(response.getBody(), OpenApiDetailDTO[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //json parsing
        ResponseDTO responseDTO = new ResponseDTO();

        OpenApiDetailDTO dto = detailDTO[0];
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

    }


}

