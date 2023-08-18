package com.umc.FestieBE.domain.open_performance.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.open_performance.dto.DetailDTO;
import com.umc.FestieBE.domain.open_performance.dto.OpenPerformanceDTO;
import com.umc.FestieBE.domain.open_performance.dto.PerformanceResponseDTO;
import com.umc.FestieBE.domain.view.application.ViewService;
import com.umc.FestieBE.domain.view.dao.ViewRepository;
import com.umc.FestieBE.domain.view.domain.View;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.DurationType;
import com.umc.FestieBE.global.type.OCategoryType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OpenPerformanceService {

    private final OpenPerformanceRepository openPerformanceRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final ViewRepository viewRepository;
    private final ViewService viewService;

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
        long totalCount = openPerformanceRepository.countTogether(categoryType,regionType,durationType);

        return new PerformanceResponseDTO.PerformanceListResponse(data,totalCount,pageNum,hasNext,hasPrevious);
    }

    //공연 상세보기
    public String getPerformanceDatail(String performanceId, Long userId){
        //Openapi 호출
        String Url = "http://www.kopis.or.kr/openApi/restful/pblprfr/";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Url)
                .path(performanceId)
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
        PerformanceResponseDTO.DetailResponseDTO detailResponseDTO = new PerformanceResponseDTO.DetailResponseDTO();
        DetailDTO dto = detailDTO[0];

        //조회수 업데이트
        viewService.updateViewCount(performanceId);

        String id = dto.getMt20id();
        String name = dto.getPrfnm();
        String profile = dto.getPoster();
        String startdate = dto.getPrfpdfrom();
        String enddate = dto.getPrfpdto();
        String datetime = dto.getDtguidance();
        String runtime = dto.getPrfruntime();
        String location = dto.getFcltynm();
        String details = dto.getSty();
        String images = dto.getStyurls().toString();
        String management = dto.getEntrpsnm();
        String price = dto.getPcseguidance();
        Long views = viewRepository.findByIdWithCount(performanceId);

        //좋아요수
        Long likes = likeOrDislikeRepository.findByTargetIdTestWithStatus(1,null,null,null, performanceId);
        Long dislikes = likeOrDislikeRepository.findByTargetIdTestWithStatus(0,null,null,null,performanceId);
        detailResponseDTO.setLikes(likes);
        detailResponseDTO.setDislikes(dislikes);
        // 좋아요/싫어요 내역 조회
        Long findLikes = likeOrDislikeRepository.findLikeOrDislikeStatus(userId,null,null,null,performanceId);

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
        detailResponseDTO.setIsWriter(findLikes);
        detailResponseDTO.setView(views);

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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

                // 가져온 데이터를 데이터 모델 객체에 매핑
                OpenPerformance performance = new OpenPerformance();
                OCategoryType categoryType = OCategoryType.findCategoryType(dto.getGenrenm());
                DurationType durationType = DurationType.findDurationType(dto.getPrfstate());

                performance.setId(dto.getMt20id());
                performance.setFestivalTitle(dto.getPrfnm());
                performance.setStartDate(LocalDate.parse(dto.getPrfpdfrom(), formatter));
                //performance.setStartDate(dto.getPrfpdfrom());
                performance.setEndDate(LocalDate.parse(dto.getPrfpdto(), formatter));
                //performance.setEndDate(dto.getPrfpdto());
                performance.setLocation(dto.getFcltynm());
                performance.setDetailUrl(dto.getPoster());
                performance.setOCategoryType(categoryType);
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

    @Scheduled(cron = "0 0 0 * * ")//매일 자정
    public void updateDataDaily() throws ParseException{
        //기존 데이터 모두 삭제
        openPerformanceRepository.deleteAll();

        //새로운 데이터 가져오기
        getAndSaveAllPerform();
    }

    //얘네는 정렬을 위해서 필요한것// 매일 업데이트가 되면서 openperformance에선 지워지기 때문에
    //좋아요 업데이트
    @Scheduled(cron = "0 50 0 * * *")
    public void updateLikeCount(){
        List<OpenPerformance> performances = openPerformanceRepository.findAll();

        for(OpenPerformance performance : performances){
            Long likeCount = likeOrDislikeRepository.findByTargetIdTestWithStatus(1,null,null,null,performance.getId());
            Long dislikeCount = likeOrDislikeRepository.findByTargetIdTestWithStatus(0,null,null,null,performance.getId());
            performance.setLikes(likeCount);
            performance.setDislikes(dislikeCount);
            openPerformanceRepository.save(performance);
        }
    }

    //view 업데이트 //매일 새벽 1시에 업로드한다고 생각하고
    @Scheduled(cron = "0 0 1 * * *")
    public void updateViewCount(){
       List<View> views = viewRepository.findAll();

       for (View view : views){
           OpenPerformance openPerformance = openPerformanceRepository.findById(view.getOpenperformance().getId())
                   .orElseThrow(() -> new CustomException(CustomErrorCode.OPEN_NOT_FOUND));

           if(openPerformance !=null){
               openPerformance.setView(view.getView());
               openPerformanceRepository.save(openPerformance);
           }
       }
    }


}
