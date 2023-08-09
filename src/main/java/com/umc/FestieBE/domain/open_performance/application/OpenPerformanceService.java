package com.umc.FestieBE.domain.open_performance.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import com.umc.FestieBE.domain.open_performance.dto.PerformanceResponseDTO;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.RegionType;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.FESTIVAL_NOT_FOUND;
import static com.umc.FestieBE.global.type.FestivalType.FESTIVAL;
import static com.umc.FestieBE.global.type.FestivalType.PERFORMANCE;


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

    //공연 목록 불러오기
    public PerformanceResponseDTO.PerformanceResponse getPerformance(
            int page, String category, String region, String duration, String sortBy
    ){
        //ENUM 타입 (categoryType, regionType)
        CategoryType categoryType = null;
        if (category !=null){
                categoryType = CategoryType.findCategoryType(category);
        }
        RegionType regionType =null;
        if(region != null){
            regionType = RegionType.findRegionType(region);
        }

        PageRequest pageRequest = PageRequest.of(page, 3);

        Slice<OpenPerformance> result = openPerformanceRepository.findAllPerformance(pageRequest, categoryType, sortBy, regionType, duration);
        //dto 매핑
        List<PerformanceResponseDTO.PerformanceDetailResponse> data = result.stream()
                .map(openPerformance -> new PerformanceResponseDTO.PerformanceDetailResponse(openPerformance))
                .collect(Collectors.toList());
        int pageNum = result.getNumber();
        boolean hasNext = result.hasNext();
        boolean hasPrevious = result.hasPrevious();

        long totalCount = openPerformanceRepository.countTogether(categoryType,regionType,duration);

        return new PerformanceResponseDTO.PerformanceResponse(data,totalCount,pageNum,hasNext,hasPrevious);
    }



    //공연 초기화 및 업데이트
    public void getAndSaveAllPerform() throws ParseException {
        int page =1;
        int rows =15;
        //한주 전과 한달 후 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        LocalDate oneWeekAgo = currentDate.minusWeeks(1);
        LocalDate oneMonthLater = currentDate.plusMonths(1);

        String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr";

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

    //디데이 설정 메서드
//    public String calculateDday(Long festivalId){
//        Festival festival = festivalRepository.findById(festivalId)
//                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));
//
//        LocalDate startDate = festival.getStartDate();
//        LocalDate endDate = festival.getEndDate();
//        LocalDate currentDate = LocalDate.now(); // 유저 로컬 날짜
//
//        Long dDayCount = ChronoUnit.DAYS.between(currentDate, startDate);
//
//        String dDay = "";
//        String type = festival.getType().getType(); // 축제 or 공연
//
//        if (PERFORMANCE == festival.getType() || FESTIVAL == festival.getType()) {
//            if (currentDate.isBefore(startDate)) {
//                dDay = "D-" + dDayCount;
//            } else if (currentDate.isAfter(endDate)) {
//                dDay = type + "종료";
//            } else {
//                dDay = type + "중";
//            }
//        }
//
//        return dDay;
//    }

}
