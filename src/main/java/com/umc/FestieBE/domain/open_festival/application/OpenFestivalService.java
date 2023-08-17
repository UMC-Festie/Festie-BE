package com.umc.FestieBE.domain.open_festival.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.open_festival.dto.OpenFestivalDTO;
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
@RequiredArgsConstructor
public class OpenFestivalService {

    private final OpenFestivalRepository openFestivalRepository;


    @Value("${openapi.FIXED_API_KEY}")
    private String FIXED_API_KEY;
    //OpenAPI 호출
    RestTemplate restTemplate = new RestTemplate();

    //공연 목록 불러오기
    public FestivalResponseDTO.FestivalListResponse getFestival(
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
        PageRequest pageRequest = PageRequest.of(page, 8);//최신순 기본정렬
        Slice<OpenFestival> result = openFestivalRepository.findAllFestival(pageRequest, categoryType, regionType, durationType, sortBy);
        //dto 매핑
        List<FestivalResponseDTO.FestivalDetailResponse> data = result.stream()
                .map(openFestival -> new FestivalResponseDTO.FestivalDetailResponse(openFestival))
                .collect(Collectors.toList());
        int pageNum = result.getNumber();
        boolean hasNext = result.hasNext();
        boolean hasPrevious = result.hasPrevious();
        long totalCount = openFestivalRepository.countTogether(categoryType,regionType,durationType);

        return new FestivalResponseDTO.FestivalListResponse(data,totalCount,pageNum,hasNext,hasPrevious);
    }

    //공연 초기화 및 업데이트
    public void getAndSaveAllFestie(String signgucode) throws ParseException {
        int page =1;
        int rows =15;
        //한주 전과 한달 후 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        LocalDate oneWeekAgo = currentDate.minusWeeks(1);
        LocalDate oneMonthLater = currentDate.plusWeeks(1);

        String apiUrl = "http://www.kopis.or.kr/openApi/restful/prffest";

        while (true) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("service", FIXED_API_KEY) // 서비스키를 고정값으로 추가
                    .queryParam("stdate", oneWeekAgo.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .queryParam("eddate", oneMonthLater.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .queryParam("cpage", page)
                    .queryParam("rows", rows)
                    .queryParam("signgucode", signgucode);

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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

                // 가져온 데이터를 데이터 모델 객체에 매핑
                OpenFestival festival = new OpenFestival();
                OCategoryType categoryType = OCategoryType.findCategoryType(dto.getGenrenm());
                DurationType durationType = DurationType.findDurationType(dto.getPrfstate());
                RegionType regionType = RegionType.findRegionType(getRegionFromSign(signgucode));

                festival.setId(dto.getMt20id());
                festival.setFestivalTitle(dto.getPrfnm());
                festival.setStartDate(LocalDate.parse(dto.getPrfpdfrom(), formatter));
                festival.setEndDate(LocalDate.parse(dto.getPrfpdto(), formatter));
                festival.setLocation(dto.getFcltynm());
                festival.setRegion(regionType);
                festival.setDetailUrl(dto.getPoster());
                festival.setOCategoryType(categoryType);
                festival.setDuration(durationType);
                festival.setOpen(dto.getFestival());

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
        getAndSaveAllFestie("11");
        getAndSaveAllFestie("28");
        getAndSaveAllFestie("30");
        getAndSaveAllFestie("27");
        getAndSaveAllFestie("29");
        getAndSaveAllFestie("26");
        getAndSaveAllFestie("31");
        getAndSaveAllFestie("36");
        getAndSaveAllFestie("41");
        getAndSaveAllFestie("43"); //충북
        getAndSaveAllFestie("44"); //충남
        getAndSaveAllFestie("47"); //경북
        getAndSaveAllFestie("48"); //경남
        getAndSaveAllFestie("45"); //전북
        getAndSaveAllFestie("46"); //전남
        getAndSaveAllFestie("51");
        getAndSaveAllFestie("50");
        getAndSaveAllFestie("UNI");
    }

    private String getRegionFromSign(String signgucode){
        if(signgucode == "11"){
            return "서울";
        }else if (signgucode == "28"){
            return "인천";
        }else if (signgucode == "30"){
            return "대전";
        }else if (signgucode == "27"){
            return "대구";
        }else if (signgucode == "29"){
            return "광주";
        }else if (signgucode == "26"){
            return "부산";
        }else if (signgucode == "31"){
            return "울산";
        }else if (signgucode == "36"){
            return "세종";
        }else if (signgucode == "41"){
            return "경기";
        }else if (signgucode == "43"){
            return "충청";
        }else if (signgucode == "44"){
            return "충청";
        }else if (signgucode == "47"){
            return "경상";
        }else if (signgucode == "48"){
            return "경상";
        }else if (signgucode == "45"){
            return "전라";
        }else if (signgucode == "46"){
            return "전라";
        }else if (signgucode == "51"){
            return "강원";
        }else if (signgucode == "50"){
            return "제주";
        }else if (signgucode == "UNI"){
            return "대학로";
        }
        return signgucode;
    }


}
