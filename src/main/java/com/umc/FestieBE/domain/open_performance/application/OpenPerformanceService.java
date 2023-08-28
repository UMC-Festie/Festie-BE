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
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
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
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

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
        String durationString = null;
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
    public String getPerformanceDetail(String performanceId, HttpServletRequest request){
        OpenPerformance openperformance = openPerformanceRepository.findById(performanceId)
                .orElseThrow(()-> (new CustomException(CustomErrorCode.OPEN_NOT_FOUND)));

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
        Long userId = jwtTokenProvider.getUserIdByServlet(request);
        PerformanceResponseDTO.DetailResponseDTO detailResponseDTO = new PerformanceResponseDTO.DetailResponseDTO();
        DetailDTO dto = detailDTO[0];

        //조회수 업데이트
        viewService.updatePerformViewCount(performanceId);

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
        Long views = viewRepository.findByIdWithCount(performanceId,null);

        //좋아요수
        Long likes = likeOrDislikeRepository.findByTargetIdTestWithStatus(1,null,null,null, performanceId,null);
        Long dislikes = likeOrDislikeRepository.findByTargetIdTestWithStatus(0,null,null,null,performanceId, null);
        detailResponseDTO.setLikes(likes);
        detailResponseDTO.setDislikes(dislikes);
        // 좋아요/싫어요 내역 조회
        Long findLikes = likeOrDislikeRepository.findLikeOrDislikeStatus(userId,null,null,null,performanceId,null);

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
        openperformance.setView(views);
        openPerformanceRepository.save(openperformance);

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
    public void getAndSaveAllPerform(String signgucode) throws ParseException {
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
                RegionType regionType = RegionType.findRegionType(getRegionFromSign(signgucode));
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

                performance.setId(dto.getMt20id());
                performance.setFestivalTitle(dto.getPrfnm());

                performance.setStartDate(LocalDate.parse(dto.getPrfpdfrom(),formatter));
                performance.setEndDate(LocalDate.parse(dto.getPrfpdto(), formatter));
                performance.setLocation(dto.getFcltynm());
                performance.setDetailUrl(dto.getPoster());
                performance.setOCategoryType(categoryType);
                performance.setDuration(durationType);
                performance.setOpenrun(dto.getOpenrun());
                performance.setRegion(regionType);

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
        getAndSaveAllPerform("11");
        getAndSaveAllPerform("28");
        getAndSaveAllPerform("30");
        getAndSaveAllPerform("27");
        getAndSaveAllPerform("29");
        getAndSaveAllPerform("26");
        getAndSaveAllPerform("31");
        getAndSaveAllPerform("36");
        getAndSaveAllPerform("41");
        getAndSaveAllPerform("43"); //충북
        getAndSaveAllPerform("44"); //충남
        getAndSaveAllPerform("47"); //경북
        getAndSaveAllPerform("48"); //경남
        getAndSaveAllPerform("45"); //전북
        getAndSaveAllPerform("46"); //전남
        getAndSaveAllPerform("51");
        getAndSaveAllPerform("50");
        getAndSaveAllPerform("UNI");

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

    //얘네는 정렬을 위해서 필요한것// 매일 업데이트가 되면서 openperformance에선 지워지기 때문에
    //좋아요 업데이트
    @Scheduled(cron = "0 50 0 * * *")
    public void updateLikeCount(){
        List<OpenPerformance> performances = openPerformanceRepository.findAll();

        for(OpenPerformance performance : performances){
            Long likeCount = likeOrDislikeRepository.findByTargetIdTestWithStatus(1,null,null,null, performance.getId(),null);
            Long dislikeCount = likeOrDislikeRepository.findByTargetIdTestWithStatus(0,null,null,null,performance.getId(),null);
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

    public String mapDurationType(DurationType durationType){
        switch (durationType){
            case ING:
                return "공연중";
            case WILL:
                return "공연예정";
            case END:
                return "공연완료";
            default:
                return "";
        }
    }


}
