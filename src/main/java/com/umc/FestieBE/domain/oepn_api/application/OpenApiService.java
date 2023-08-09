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
   //공연 정보보기
    public String getPerform(Integer startDate, Integer endDate, Integer currentpage, Integer rows, String category, String region, String period, Integer sort) throws ParseException {
        //OpenAPI 호출을 위한 URL 생성
        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                .queryParam("stdate", startDate)
                .queryParam("eddate", endDate)
                .queryParam("cpage", currentpage)
                .queryParam("rows", rows)
                .queryParam("shcate", category)
                .queryParam("signgucode", region)
                .queryParam("prfstate", period);

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
        PerformanceDTO[] events;
        try {
            events = xmlMapper.readValue(response.getBody(), PerformanceDTO[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        int dataSize = events.length;
        PerformResponseDTO[] performResponseDTOArray = new PerformResponseDTO[dataSize];

            // events 배열 크기만큼 for문으로 각 객체의 정보를 가져와서 설정
        for (int i =0; i< dataSize; i++) {
            PerformanceDTO event = events[i];
            PerformResponseDTO performResponseDTO = new PerformResponseDTO();

            String id = event.getMt20id();
            String name = event.getPrfnm();
            String profile = event.getPoster();
            String location = event.getFcltynm();
            String startDateStr = event.getPrfpdfrom();
            String endDateStr = event.getPrfpdto();
            String state = event.getPrfstate();
            String genrenm = event.getGenrenm();

            performResponseDTO.setId(id);
            performResponseDTO.setName(name);
            performResponseDTO.setState(state);
            performResponseDTO.setLocation(location);
            performResponseDTO.setStartDate(startDateStr);
            performResponseDTO.setEndDate(endDateStr);
            performResponseDTO.setProfile(profile);
            performResponseDTO.setGenrenm(genrenm);
            performResponseDTO.setTotalcount(dataSize);

            performResponseDTOArray[i] = performResponseDTO;
        }

        //json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(performResponseDTOArray);
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
        DetailDTO[] detailDTO;
        try {
            detailDTO = xmlMapper.readValue(response.getBody(), DetailDTO[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //json parsing
        DetailResponseDTO detailResponseDTO = new DetailResponseDTO();

        DetailDTO dto = detailDTO[0];
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

        detailResponseDTO.setId(id);
        detailResponseDTO.setName(name);
        detailResponseDTO.setProfile(profile);
        detailResponseDTO.setStartDate(startdate);
        detailResponseDTO.setEndDate(enddate);
        detailResponseDTO.setDateTime(datetime);
        detailResponseDTO.setRuntime(runtime);
        detailResponseDTO.setLocation(location);
        detailResponseDTO.setDetails(details);
        detailResponseDTO.setImages(images);
        detailResponseDTO.setManagement(management);
        detailResponseDTO.setPrice(price);

        //json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(detailResponseDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return jsonResult;

    }


    //축제 정보보기
    public String getFestie(Integer startDate, Integer endDate, Integer currentpage, Integer rows, String category, String region, String period, Integer sort) throws ParseException {
        //OpenAPI 호출을 위한 URL 생성
        String apiUrl = "http://kopis.or.kr/openApi/restful/prffest";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                .queryParam("stdate", startDate)
                .queryParam("eddate", endDate)
                .queryParam("cpage", currentpage)
                .queryParam("rows", rows)
                .queryParam("shcate", category)
                .queryParam("signgucode",region)
                .queryParam("prfstate", period);


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
        FestievalDTO[] events;
        try {
            events = xmlMapper.readValue(response.getBody(), FestievalDTO[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        FestievalResponseDTO[] responseDTOArray = new FestievalResponseDTO[events.length];
        // events 배열 크기만큼 for문으로 각 객체의 정보를 가져와서 설정
        for (int i =0; i< events.length; i++) {
            FestievalDTO event = events[i];
            FestievalResponseDTO responseDTO = new FestievalResponseDTO();

            String id = event.getMt20id();
            String name = event.getPrfnm();
            String profile = event.getPoster();
            String location = event.getFcltynm();
            String startDateStr = event.getPrfpdfrom();
            String endDateStr = event.getPrfpdto();
            String state = event.getPrfstate();
            String genrenm = event.getGenrenm();

            responseDTO.setId(id);
            responseDTO.setName(name);
            responseDTO.setState(state);
            responseDTO.setLocation(location);
            responseDTO.setStartDate(startDateStr);
            responseDTO.setEndDate(endDateStr);
            responseDTO.setProfile(profile);
            responseDTO.setGenrenm(genrenm);

            responseDTOArray[i] = responseDTO;
        }

        //json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(responseDTOArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return jsonResult;

    }

}

