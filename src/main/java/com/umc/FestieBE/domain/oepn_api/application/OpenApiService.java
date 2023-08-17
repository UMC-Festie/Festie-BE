package com.umc.FestieBE.domain.oepn_api.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.umc.FestieBE.domain.oepn_api.dao.OpenApiRepository;
import com.umc.FestieBE.domain.oepn_api.domain.OpenApi;
import com.umc.FestieBE.domain.oepn_api.dto.*;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


//비즈니스 로직을 담당하는 계층, controller와 repository사이에서 데이터 처리를 담당.
//controller에서 받은 요청을 처리하고, 필요한 데이터를 dto로 변환하여 반환하는 역할.
@Service
@RequiredArgsConstructor
public class OpenApiService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OpenApiRepository openApiRepository;

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
    public String getPerformanceDetail(String mt20id, HttpServletRequest request) {
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

        // 최근 내역 조회를 위해 유저 정보 가져옴
        Long userId = jwtTokenProvider.getUserIdByServlet(request);
        if(userId != null) {
            List<Map<String, String>> recentOpenAPIs = getRecentOpenAPIs(userId);
            Map<String, String> openAPIsInfo = openAPIsToMap(detailResponseDTO);
            updateRecentOpenAPIs(userId, recentOpenAPIs, openAPIsInfo);
            saveRecentOpenAPI(userId, recentOpenAPIs);
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


    /** (Redis) 최근 내역 조회 */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveRecentOpenAPI(Long userId, List<Map<String, String>> openAPIs) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String cacheKey = "recentOpenAPIs:" + userId;
        ObjectMapper objectMapper = new ObjectMapper();
        String openAPIsJson;

        int maxRecentOpenAPIs = 8;
        if(openAPIs.size() > maxRecentOpenAPIs) {
            openAPIs = openAPIs.subList(openAPIs.size() - maxRecentOpenAPIs, openAPIs.size());
        }

        try {
            openAPIsJson = objectMapper.writeValueAsString(openAPIs);
            Duration expiration = Duration.ofDays(7);
            vop.set(cacheKey, openAPIsJson, expiration);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> getRecentOpenAPIs(Long userId) {
        String cacheKey = "recentOpenAPIs" + userId;
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String openAPIsJson = vop.get(cacheKey);

        if(openAPIsJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Map<String, String>> openAPIs = objectMapper.readValue(openAPIsJson, new TypeReference<List<Map<String, String>>>() {});
                Collections.reverse(openAPIs);
                return openAPIs;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }



    /** 축제 최근 조회 내역 */
    private Map<String, String> openAPIsToMap(DetailResponseDTO detailResponseDTO) {
        Map<String, String> openAPIsInfo = new HashMap<>();

        String startDateStr = detailResponseDTO.getStartDate();
        LocalDate startDate = LocalDate.parse(startDateStr); // 시작 날짜
        String endDateStr = detailResponseDTO.getEndDate();
        LocalDate endDate = LocalDate.parse(endDateStr); // 종료 날짜
        LocalDate currentDate = LocalDate.now(); // 유저 로컬 날짜

        Long dDayCount = ChronoUnit.DAYS.between(currentDate, startDate);

        String dDay = "";
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

        openAPIsInfo.put("openAPIsId", detailResponseDTO.getId());
        openAPIsInfo.put("openAPIsFestivalTitle", detailResponseDTO.getName());
        // openAPIsInfo.put("openAPIsDuration", detailResponseDTO.);
        openAPIsInfo.put("openAPIsThumbnailsUrl", detailResponseDTO.getImages());
        openAPIsInfo.put("openAPIsLocation", detailResponseDTO.getLocation());
        openAPIsInfo.put("openAPIsFestivalDate", detailResponseDTO.getStartDate() + " - " + detailResponseDTO.getEndDate());
        // openAPIsInfo.put("openAPIsFestivalType", detailResponseDTO.);

        return openAPIsInfo;
    }

    private void updateRecentOpenAPIs(Long userId, List<Map<String, String>> recentOpenAPIs, Map<String, String> newOpenAPIsInfo) {
        String newOpenAPIsId = newOpenAPIsInfo.get("openAPIsId");

        for(Map<String, String> openAPIsInfo : recentOpenAPIs) {
            if(openAPIsInfo.get("openAPIsId").equals(newOpenAPIsId)) {
                openAPIsInfo.putAll(newOpenAPIsInfo);
                saveRecentOpenAPI(userId, recentOpenAPIs);
                return;
            }
        }
        recentOpenAPIs.add(newOpenAPIsInfo);
        saveRecentOpenAPI(userId, recentOpenAPIs);
    }
}

