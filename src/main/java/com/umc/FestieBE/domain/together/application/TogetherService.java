package com.umc.FestieBE.domain.together.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.FestieBE.domain.applicant_info.dao.ApplicantInfoRepository;
import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoResponseDTO;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;

import com.umc.FestieBE.domain.festival.dto.FestivalSearchResponseDTO;
import com.umc.FestieBE.domain.oepn_api.dto.FestivalListResponseDTO;

import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.HomeResponseDTO;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.image.AwsS3Service;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.DurationType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import jdk.security.jarsigner.JarSignerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;
import static com.umc.FestieBE.global.type.DurationType.*;
import static com.umc.FestieBE.global.type.FestivalType.findFestivalType;

@Service
@RequiredArgsConstructor
@Slf4j
public class TogetherService {

    private final TogetherRepository togetherRepository;
    private final FestivalRepository festivalRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final OpenFestivalRepository openFestivalRepository;
    private final ApplicantInfoRepository applicantInfoRepository;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final AwsS3Service awsS3Service;


    /** (Redis) 같이가요 최근 조회 내역 */
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 최근 내역 저장
    public void saveRecentTogethers(Long userId, List<Map<String, String>> togethers) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String cacheKey = "recentTogethers:" + userId;
        ObjectMapper objectMapper =  new ObjectMapper();
        String togethersJson;

        int maxRecentTogether = 8;
        if (togethers.size() > maxRecentTogether) {
            togethers = togethers.subList(togethers.size() - maxRecentTogether, togethers.size());
        }

        try {
            togethersJson = objectMapper.writeValueAsString(togethers);
            Duration expration = Duration.ofDays(7); // 7일뒤 캐시 자동 삭제
            vop.set(cacheKey, togethersJson, expration);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // 최근 내역 가져오기
    public List<Map<String, String>> getRecentTogethers(Long userId) {
        String cacheKey = "recentTogethers:" + userId;
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String togethersJson = vop.get(cacheKey);

        if (togethersJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Map<String, String>> togethers = objectMapper.readValue(togethersJson, new TypeReference<List<Map<String, String>>>() {});
                Collections.reverse(togethers);
                return togethers;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    // 최근 조회 내역 Map 형식으로 변경
    private Map<String, String> togetherToMap(Together together) {
        Map<String, String> togetherInfo = new HashMap<>();

        String isApplicationSuccess = null; // 매칭 성공 여부
        Optional<ApplicantInfo> findApplication = applicantInfoRepository.findByTogetherIdAndUserId(together.getId(), together.getUser().getId());

        if (findApplication.isPresent()) {
            isApplicationSuccess = findApplication.get().getIsSelected().toString();
        }

        togetherInfo.put("togetherId", together.getId().toString());
        togetherInfo.put("togetherTitle", together.getTitle());
        togetherInfo.put("updatedAt", together.getUpdatedAt().toString());
        togetherInfo.put("thumbnailUrl", together.getThumbnailUrl());
        togetherInfo.put("writerNickname", together.getUser().getNickname());
        togetherInfo.put("status", together.getStatus().toString()); // 0: 매칭 대기중, 1: 매칭 완료
        togetherInfo.put("isApplicationSuccess", isApplicationSuccess); // 신청 성공 여부

        return togetherInfo;
    }

    // 최근 조회 내역 업데이트
    private void updateRecentTogethers(Long userId, List<Map<String, String>> recentTogethers, Map<String, String> newTogetherInfo) {
        String newTogetherId = newTogetherInfo.get("togetherId");

        for(Map<String, String> togetherInfo : recentTogethers) {
            if(togetherInfo.get("togetherId").equals(newTogetherId)) {
                togetherInfo.putAll(newTogetherInfo);
                saveRecentTogethers(userId, recentTogethers);
                return;
            }
        }

        recentTogethers.add(newTogetherInfo);
        saveRecentTogethers(userId, recentTogethers);
    }


    /**
     * 같이가요 게시글 등록
     */
    public void createTogether(TogetherRequestDTO.TogetherRequest request, MultipartFile thumbnail){
        // 유저
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));


        // 공연/축제 정보 연동 시 DB 에서 확인
        if (request.getFestivalId() != null) {
            checkIfFestivalIdExists(request.getFestivalId(), request.getBoardType(), request.getFestivalType());
        }

        // 같이가요 게시글 등록
        FestivalType festivalType = findFestivalType(request.getFestivalType());
        CategoryType categoryType = CategoryType.findCategoryType(request.getCategory());
        RegionType regionType = RegionType.findRegionType(request.getRegion());

        String imgUrl = null;
        if(thumbnail != null){
            imgUrl = awsS3Service.uploadImgFile(thumbnail);
        }

        Together together = request.toEntity(user, festivalType, categoryType, regionType, imgUrl);
        togetherRepository.save(together);
    }

    private void checkIfFestivalIdExists(String festivalId, String boardType, String festivalType){
        switch (boardType) {
            case "정보공유":
                festivalRepository.findById(Long.valueOf(festivalId))
                        .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));
                break;

            case "정보보기":
                if ("공연".equals(festivalType)) {
                    openPerformanceRepository.findById(festivalId);
                } else if ("축제".equals(festivalType)) {
                    openFestivalRepository.findById(festivalId);
                }
                break;

            default:
                throw new CustomException(INVALID_VALUE, "공연/축제 게시글 유형은 '정보보기' 또는 '정보공유'만 가능합니다.");
        }
    }

    /**
     * 같이가요 게시글 상세 조회
     */
    public TogetherResponseDTO.TogetherDetailResponse getTogether(Long togetherId, HttpServletRequest request){
        // 조회수 업데이트
        togetherRepository.updateView(togetherId);

        // 같이가요 게시글 조회
        Together together = togetherRepository.findByIdWithUser(togetherId)
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 유저 확인 (게시글 작성자인지 / 신청자인지 / 신청 결과)
        boolean isWriter = false;
        boolean isApplicant = false;
        boolean isApplicationSuccess = false;

        Long userId = jwtTokenProvider.getUserIdByServlet(request);
        if(userId != null) { //로그인한 유저인 경우
            if (userId == together.getUser().getId()) { //작성자인 경우
                isWriter = true;
            } else {
                // Bestie 신청 여부
                Optional<ApplicantInfo> findApplication = applicantInfoRepository.findByTogetherIdAndUserId(togetherId, userId);
                if (findApplication.isPresent()) { //Bestie 신청 O
                    isApplicant = true;
                    isApplicationSuccess = findApplication.get().getIsSelected();
                }
            }
        }

        // Bestie 신청 내역
        List<ApplicantInfo> applicantInfoList = applicantInfoRepository.findByTogetherIdWithUser(togetherId);
        List<ApplicantInfoResponseDTO> applicantList  = applicantInfoList.stream()
                .map(ApplicantInfoResponseDTO::new)
                .collect(Collectors.toList());

        // festival 정보 및 연동 여부
        boolean isLinked = false;
        boolean isDeleted = false;
        FestivalLinkResponseDTO festivalInfo = null;

        // 공연/축제 연동 O
        String festivalId = together.getFestivalId();
        if (festivalId != null) {
            isLinked = true;

            if(together.getBoardType().equals("정보공유")){
                Festival linkedFestival = festivalRepository.findById(Long.valueOf(festivalId))
                        .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
                //삭제 되었을 경우
                if(linkedFestival.getIsDeleted()){ isDeleted = true; }
                festivalInfo = new FestivalLinkResponseDTO(linkedFestival);
            }else if(together.getBoardType().equals("정보보기")){
                if(together.getType().getType().equals("공연")){
                    OpenPerformance openPerformance = openPerformanceRepository.findById(festivalId)
                            .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
                    festivalInfo = new FestivalLinkResponseDTO(openPerformance);
                }else if(together.getType().getType().equals("축제")){
                    OpenFestival openFestival = openFestivalRepository.findById(festivalId)
                            .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
                    festivalInfo = new FestivalLinkResponseDTO(openFestival);
                }
            }
        }
        // 공연/축제 연동 X (직접 입력)
        else {
            festivalInfo = new FestivalLinkResponseDTO(together);
        }

        TogetherResponseDTO.TogetherDetailResponse togetherDetailResponse;

        /** 상세 조회 시 최근 조회 내역 캐시에 저장 */
        if (userId != null) {
            List<Map<String, String>> recentTogethers = getRecentTogethers(userId);
            Map<String, String> togethersInfo = togetherToMap(together);
            updateRecentTogethers(userId, recentTogethers, togethersInfo);
            saveRecentTogethers(userId, recentTogethers);

            togetherDetailResponse = new TogetherResponseDTO.TogetherDetailResponse(together, applicantList, isLinked, isDeleted, festivalInfo,
                    isWriter, isApplicant, isApplicationSuccess);
        } else {
            togetherDetailResponse = new TogetherResponseDTO.TogetherDetailResponse(together, applicantList, isLinked, isDeleted, festivalInfo,
                    isWriter, isApplicant, isApplicationSuccess);
        }

        return togetherDetailResponse;
    }


    /**
     * 같이가요 게시글 수정
     */
    @Transactional
    public void updateTogether(Long togetherId,
                               TogetherRequestDTO.TogetherRequest request, MultipartFile thumbnail){

        // 같이가요 게시글 조회
        Together together = togetherRepository.findById(togetherId)
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 게시글 수정 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != together.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "같이가요 게시글 수정 권한이 없습니다.");
        }

        // 공연/축제 정보 연동 시 DB 에서 확인
        if (request.getFestivalId() != null) {
            checkIfFestivalIdExists(request.getFestivalId(), request.getBoardType(), request.getFestivalType());
        }

        // 게시글 수정 반영
        String imgUrl = null;
        if(thumbnail != null){
            imgUrl = awsS3Service.uploadImgFile(thumbnail);
        }
        together.updateTogether(request, imgUrl);
    }


    /**
     * 같이가요 게시글 삭제
     */
    public void deleteTogether(Long togetherId){
        // 같이가요 게시글 조회
        Together together = togetherRepository.findById(togetherId)
            .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 게시글 삭제 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != together.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "같이가요 게시글 삭제 권한이 없습니다.");
        }

        // Bestie 신청 내역 삭제
        applicantInfoRepository.deleteByTogetherId(togetherId);

        // 같이가요 게시글 삭제
        awsS3Service.deleteImage(together.getThumbnailUrl());
        togetherRepository.deleteById(togetherId);
    }


    /**
     * 같이가요 게시글 목록 조회
     */
    public TogetherResponseDTO.TogetherListResponse getTogetherList
        (int page, String type, String category, String region, String status, String sort){

        // ENUM 타입 (festivalType, regionType, categoryType)
        FestivalType festivalType = null;
        if(type != null){
            festivalType = findFestivalType(type);
        }
        RegionType regionType = null;
        if(region != null){
            regionType = RegionType.findRegionType(region);
        }
        CategoryType categoryType = null;
        if(category != null){
            categoryType = CategoryType.findCategoryType(category);
        }

        // 매칭 상태(status)
        Integer statusType;
        if(status.equals("모집중")){
            statusType = 0;
        }else if(status.equals("모집종료")){
            statusType = 1;
        }else{
            throw new CustomException(CustomErrorCode.INVALID_VALUE, "해당하는 모집 상태가 없습니다. (모집중/모집종료)");
        }

        PageRequest pageRequest = PageRequest.of(page, 16);
        Slice<Together> result = togetherRepository.findAllTogether(pageRequest, festivalType, categoryType, regionType, statusType, sort);
        List<TogetherResponseDTO.TogetherListDetailResponse> data = result.stream()
                .map(together -> new TogetherResponseDTO.TogetherListDetailResponse(together))
                .collect(Collectors.toList());
        int pageNum = result.getNumber();
        boolean hasNext = result.hasNext();
        boolean hasPrevious = result.hasPrevious();

        long totalCount = togetherRepository.countTogether(festivalType, categoryType, regionType, statusType);

        return new TogetherResponseDTO.TogetherListResponse(data, totalCount, pageNum, hasNext, hasPrevious);
    }

    /**
     * 같이가요 게시글 등록 시 공연/축제 연동 - 검색
     */
    public FestivalSearchResponseDTO.FestivalListResponse getFestivalSearchList(String keyword){
        if(keyword == null || keyword.trim().isEmpty()){
            throw new CustomException(KEYWORD_MISSING_ERROR);
        }

        List<FestivalSearchResponseDTO.FestivalListDetailResponse> festivalDetailResponseList = new ArrayList<>();

        // 정보공유
        List<Festival> festivalList = festivalRepository.findByFestivalTitleContaining(keyword);
        festivalDetailResponseList.addAll(festivalList.stream()
                .map(f -> new FestivalSearchResponseDTO.FestivalListDetailResponse(f, "정보공유"))
                .collect(Collectors.toList()));

        // 정보보기 (공연)
        List<OpenPerformance> openPerformanceList = openPerformanceRepository.findByFestivalTitleContaining(keyword);
        festivalDetailResponseList.addAll(openPerformanceList.stream()
                .map(op -> new FestivalSearchResponseDTO.FestivalListDetailResponse(op, "정보보기"))
                .collect(Collectors.toList()));

        // 정보보기 (축제)
        List<OpenFestival> openFestivalList = openFestivalRepository.findByFestivalTitleContaining(keyword);
        festivalDetailResponseList.addAll(openFestivalList.stream()
                .map(of -> new FestivalSearchResponseDTO.FestivalListDetailResponse(of, "정보보기"))
                .collect(Collectors.toList()));

        return new FestivalSearchResponseDTO.FestivalListResponse(festivalDetailResponseList);
    }

    /**
     * 같이가요 게시글 등록 시 공연/축제 연동 - 선택
     */
    public FestivalSearchResponseDTO.FestivalInfoResponse getFestivalSelectedInfo(String boardType, String festivalId){

        // 정보공유
        if(boardType.equals("정보공유")){
            // TODO Long.valueOf exception 추가
            Optional<Festival> festival = festivalRepository.findById(Long.valueOf(festivalId));
            if(festival.isPresent()){
                return new FestivalSearchResponseDTO.FestivalInfoResponse(festival.get());
            }
        }
        // 정보보기
        else if(boardType.equals("정보보기")){
            // 공연
            Optional<OpenPerformance> openPerformance = openPerformanceRepository.findById(festivalId);
            if(openPerformance.isPresent()){
                return new FestivalSearchResponseDTO.FestivalInfoResponse(openPerformance.get());
            }

            // 축제
            Optional<OpenFestival> openFestival = openFestivalRepository.findById(festivalId);
            if(openFestival.isPresent()){
                return new FestivalSearchResponseDTO.FestivalInfoResponse(openFestival.get());
            }
        }

        // 검색 결과가 없을 경우
        throw new CustomException(FESTIVAL_NOT_FOUND);
    }

    /**
     * 홈 화면 - 곧 다가와요 & 같이가요 목록 조회
     */
    public HomeResponseDTO getFestivalAndTogetherList(Integer festivalType, Integer togetherType){

        List<FestivalListResponseDTO.FestivalHomeListResponse> festivalResponseList = new ArrayList<>();
        List<TogetherResponseDTO.TogetherHomeListResponse> togetherResponseList = new ArrayList<>();

        if(festivalType != null){
            festivalResponseList = getFestivalHomeList(festivalType);
        }
        if(togetherType != null){
            togetherResponseList = getTogetherHomeList(togetherType);
        }

        return new HomeResponseDTO(festivalResponseList, togetherResponseList);
    }

    /* 곧 다가와요 */
    private List<FestivalListResponseDTO.FestivalHomeListResponse> getFestivalHomeList(Integer festivalType){
        List<FestivalListResponseDTO.FestivalHomeListResponse> festivalResponseList = new ArrayList<>();

        int pageSize = 4;
        Pageable pageable = (Pageable) PageRequest.of(0, pageSize);

        LocalDate currentDate = LocalDate.now();
        Integer status = null;
        Long dDay = null;

        // 공연
        if(festivalType == 0){
            List<OpenPerformance> performanceList = openPerformanceRepository.findByState(pageable, currentDate).getContent();

            for(OpenPerformance op: performanceList){
                if (op.getDuration() == WILL) {
                    dDay = ChronoUnit.DAYS.between(currentDate, op.getStartDate());
                    status = 0;
                }else if (op.getDuration() == ING){
                    status = 1;
                }
                festivalResponseList.add(new FestivalListResponseDTO.FestivalHomeListResponse(op, status, dDay));
            }
        }
        // 축제
        else if(festivalType == 1){
            List<OpenFestival> festivalList = openFestivalRepository.findByState(pageable, currentDate).getContent();

            for(OpenFestival of: festivalList){
                if (of.getDuration() == WILL) {
                    dDay = ChronoUnit.DAYS.between(currentDate, of.getStartDate());
                    status = 0;
                }else if (of.getDuration() == ING){
                    status = 1;
                }
                festivalResponseList.add(new FestivalListResponseDTO.FestivalHomeListResponse(of, status, dDay));
            }
        }
        // 그 외
        else { throw new CustomException(INVALID_VALUE, "festivalType은 0(공연) 또는 1(축제)만 가능합니다."); }

        return festivalResponseList;
    }

    /* 같이가요 */
    private List<TogetherResponseDTO.TogetherHomeListResponse> getTogetherHomeList(Integer togetherType){
        List<TogetherResponseDTO.TogetherHomeListResponse> togetherResponseList = new ArrayList<>();

        int pageSize = 4;
        Sort sort;
        Pageable pageable;

        List<Together> togetherList = new ArrayList<>();

        // 얼마 남지 않은
        if(togetherType == 0){
            sort = Sort.by(Sort.Direction.ASC, "date");
            pageable = (Pageable) PageRequest.of(0, pageSize, sort);
            togetherList = togetherRepository.findAllWithUser(pageable, 0).getContent();
        }
        // 새로운
        else if(togetherType == 1){
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
            pageable = (Pageable) PageRequest.of(0, pageSize, sort);
            togetherList = togetherRepository.findAllWithUser(pageable, null).getContent();
        }
        // 그 외
        else { throw new CustomException(INVALID_VALUE, "togetherType은 0(얼마 남지 않은) 또는 1(새로운)만 가능합니다."); }

        togetherResponseList = togetherList.stream()
                .map(t -> new TogetherResponseDTO.TogetherHomeListResponse(t))
                .collect(Collectors.toList());

        return togetherResponseList;
    }


}


