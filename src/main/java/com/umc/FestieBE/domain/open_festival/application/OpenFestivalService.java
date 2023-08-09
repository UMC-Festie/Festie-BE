package com.umc.FestieBE.domain.open_festival.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_festival.dto.OpenFestivalDTO;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class OpenFestivalService {

    private final OpenFestivalRepository openFestivalRepository;

    @Autowired
    public OpenFestivalService(OpenFestivalRepository openFestivalRepository) {
        this.openFestivalRepository = openFestivalRepository;
    }

    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //OpenAPI 호출
    RestTemplate restTemplate = new RestTemplate();
    //공연 목록
    public void getAndSaveAllFestie() throws ParseException {
        int page =1;
        int rows =15;
        //한주 전과 한달 후 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        LocalDate oneWeekAgo = currentDate.minusMonths(1);
        LocalDate oneMonthLater = currentDate.plusMonths(1);

        String apiUrl = "http://www.kopis.or.kr/openApi/restful/prffest";

        while (true) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                    .queryParam("stdate", oneWeekAgo.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .queryParam("eddate", oneMonthLater.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .queryParam("cpage", page)
                    .queryParam("rows", rows);

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
            OpenFestivalDTO[] data = parseXmlData(response.getBody());

            if (data.length == 0) {
                break;
            }

            for (OpenFestivalDTO dto : data) {
                // 가져온 데이터를 데이터 모델 객체에 매핑
                OpenFestival festival = new OpenFestival();
                festival.setId(dto.getMt20id());
                festival.setFestivalTitle(dto.getPrfnm());
                festival.setStartDate(dto.getPrfpdfrom());
                festival.setEndDate(dto.getPrfpdto());
                festival.setLocation(dto.getFcltynm());
                festival.setDetailUrl(dto.getPoster());
                festival.setGenrenm(dto.getGenrenm());
                festival.setState(dto.getPrfstate());
                festival.setFestival(dto.getFestival());

                saveDataToDB(festival);
            }
            page++;
        }

    }
    //xml을 자바 객체로 변환
    private OpenFestivalDTO[] parseXmlData(String xml){
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xml, OpenFestivalDTO[].class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    public void saveDataToDB(OpenFestival data){
        openFestivalRepository.save(data);
    }

    @Scheduled(cron = "0 0 0 * * ?")//매일 자정
    public void updateDataDaily() throws ParseException{
        //기존 데이터 모두 삭제
        openFestivalRepository.deleteAll();

        //새로운 데이터 가져오기
        getAndSaveAllFestie();
    }


}
