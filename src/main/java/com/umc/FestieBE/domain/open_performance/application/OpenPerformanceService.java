package com.umc.FestieBE.domain.open_performance.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import org.aspectj.apache.bcel.classfile.Module;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;


@Service
public class OpenPerformanceService {

    private final OpenPerformanceRepository openPerformanceRepository;

    @Autowired
    public OpenPerformanceService(OpenPerformanceRepository openPerformanceRepository) {
        this.openPerformanceRepository = openPerformanceRepository;
    }

    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //OpenAPI 호출
    RestTemplate restTemplate = new RestTemplate();
    //공연 목록
    public void getAndSaveAllPerform() throws ParseException {
        int page =1;
        int rows =8;
        //한달 전과 한달 후 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthAgo = currentDate.minusMonths(1);
        LocalDate oneMonthLater = currentDate.plusMonths(1);

        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";

        while (true) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                    .queryParam("stdate", oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .queryParam("eddate", oneMonthLater.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .queryParam("cpage", page)
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

            //XML 파싱
            OpenPerformanceDTO[] data = parseXmlData(response.getBody());

            if (data.length == 0) {
                break;
            }

            for (OpenPerformanceDTO dto : data) {
                // 가져온 데이터를 데이터 모델 객체에 매핑
                OpenPerformance performance = new OpenPerformance();
                performance.setId(dto.getMt20id());
                performance.setFestivalTitle(dto.getPrfnm());
                performance.setStartDate(dto.getPrfpdfrom());
                performance.setEndDate(dto.getPrfpdto());
                performance.setLocation(dto.getFcltynm());
                performance.setDetailUrl(dto.getPoster());
                performance.setGenrenm(dto.getGenrenm());
                performance.setState(dto.getPrfstate());
                performance.setOpenrun(dto.getOpenrun());

                saveDataToDB(performance);
            }
            page++;
        }

    }
    //xml을 자바 객체로 변환
    private OpenPerformanceDTO[] parseXmlData(String xml){
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xml, OpenPerformanceDTO[].class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveDataToDB(OpenPerformance data){
        openPerformanceRepository.save(data);
    }

    @Scheduled(cron = "0 0 0 * * ?")//매일 자정
    public void updateDataDaily() throws ParseException{
        //기존 데이터 모두 삭제
        openPerformanceRepository.deleteAll();

        //새로운 데이터 가져오기
        getAndSaveAllPerform();
    }


}