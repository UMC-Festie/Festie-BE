package com.umc.FestieBE.domain.open_performance.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.oepn_api.dto.DetailDTO;
import com.umc.FestieBE.domain.oepn_api.dto.DetailResponseDTO;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import com.umc.FestieBE.domain.open_performance.dto.PerformanceResponseDTO;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.DurationType;
import com.umc.FestieBE.global.type.OCategoryType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OpenPerformanceService {

    private final OpenPerformanceRepository openPerformanceRepository;
    @Autowired
    public OpenPerformanceService(OpenPerformanceRepository openPerformanceRepository) {
        this.openPerformanceRepository = openPerformanceRepository;}
    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //OpenAPI 호출
    RestTemplate restTemplate = new RestTemplate();

    //공연 목록 불러오기
    public PerformanceResponseDTO.PerformanceListResponse getPerformance(
            int page, String category, String region, String duration, String sortBy){
        //ENUM 타입 (categoryType, regionType)
        CategoryType categoryType = null;
        if (category !=null){
                categoryType = CategoryType.findCategoryType(category);
        }
        RegionType regionType =null;
        if(region != null){
            regionType = RegionType.findRegionType(region);
        }
        DurationType durationType =null;
        if(duration !=null){
            durationType = DurationType.findDurationType(duration);
        }

        PageRequest pageRequest = PageRequest.of(page, 8);// 최신순 기본 정렬
        Slice<OpenPerformance> result = openPerformanceRepository.findAllPerformance(pageRequest, categoryType, regionType, durationType, sortBy);
        //dto 매핑
        List<PerformanceResponseDTO.PerformanceDetailResponse> data = result.stream()
                .map(openPerformance -> new PerformanceResponseDTO.PerformanceDetailResponse(openPerformance))
                .collect(Collectors.toList());

        int pageNum = result.getNumber();
        boolean hasNext = result.hasNext();
        boolean hasPrevious = result.hasPrevious();

//      long totalCount = openPerformanceRepository.countTogether(categoryType,regionType,duration);

        return new PerformanceResponseDTO.PerformanceListResponse(data,pageNum,hasNext,hasPrevious);
    }

    //공연 상세보기
    public String getPerformanceDatail(String mt20id){
        //Openapi 호출
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


    //공연 초기화 및 업데이트
    public void getAndSaveAllPerform() throws ParseException {
        int page =1;
        int rows =15;
        //한주 전과 한달 후 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        LocalDate oneWeekAgo = currentDate.minusWeeks(1);
        LocalDate oneMonthLater = currentDate.plusWeeks(1);

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
                OCategoryType categoryType = OCategoryType.findCategoryType(dto.getGenrenm());
                DurationType durationType = DurationType.findDurationType(dto.getPrfstate());

                performance.setId(dto.getMt20id());
                performance.setFestivalTitle(dto.getPrfnm());
                performance.setStartDate(dto.getPrfpdfrom());
                performance.setEndDate(dto.getPrfpdto());
                performance.setLocation(dto.getFcltynm());
                performance.setDetailUrl(dto.getPoster());
                performance.setCategory(categoryType);
                performance.setDuration(durationType);
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
