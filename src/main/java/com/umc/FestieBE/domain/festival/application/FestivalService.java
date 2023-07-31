package com.umc.FestieBE.domain.festival.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalPaginationResponseDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalRequestDTO;
import com.umc.FestieBE.domain.festival.dto.FestivalResponseDTO;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import com.umc.FestieBE.global.type.SortedType;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.SortedMapType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.FESTIVAL_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FestivalService {
    private final FestivalRepository festivalRepository;

    // 임시 유저
    private final TemporaryUserService temporaryUserService;

    /** 새로운 공연, 축제 상세 조회 */
    public FestivalResponseDTO getFestival(FestivalService festivalService, Long festivalId){
        // 조회수 업데이트
        festivalRepository.updateView(festivalId);

        Festival festival = festivalRepository.findByIdWithUser(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        // TODO isWriter 확인

        Boolean isWriter = null;
        String dDay = festivalService.calculateDday(festivalId);

        return new FestivalResponseDTO(festival, isWriter, dDay);
    }

    /** 새로운 공연,축제 등록 */
    public void createFestival(FestivalRequestDTO request){
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        RegionType region = RegionType.findRegionType(request.getFestivalRegion());
        CategoryType category = CategoryType.findCategoryType(request.getCategory());

        Boolean isDeleted = false;

        Festival festival = request.toEntity(tempUser, festivalType, region, category, isDeleted);
        festivalRepository.save(festival);
    }

    /** 새로운 공연,축제 수정 */
    public void updateFestival(Long festivalId, FestivalRequestDTO request) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        RegionType region = RegionType.findRegionType(request.getFestivalRegion());
        CategoryType category = CategoryType.findCategoryType(request.getCategory());
        Boolean isDeleted = false;

        festival.updateFestival(
                request.getFestivalTitle(),
                festivalType,
                request.getThumbnailUrl(),
                category,
                region,
                request.getFestivalLocation(),
                request.getFestivalStartDate(),
                request.getFestivalEndDate(),
                request.getFestivalStartTime(),
                request.getPostTitle(),
                request.getContent(),
                request.getFestivalAdminsName(),
                request.getFestivalAdminsPhone(),
                request.getFestivalAdminsSiteAddress(),
                isDeleted
        );
        festivalRepository.save(festival);
    }

    /**
     * [새로운 공연, 축제 삭제]
     * 새로운 공연, 축제 삭제 시 해당 데이터가 진짜 삭제 되면 안됨
     * : 데이터 삭제 안하고 isDeleted값이 true가 되도록 함
     */
    public void deleteFestival(Long festivalId, Boolean isDeleted) {
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        festival.deleteFestival(isDeleted);
        festivalRepository.save(festival);
    }

    /** 디데이 설정 메서드 */
    public String calculateDday(Long festivalId){
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(FESTIVAL_NOT_FOUND));

        LocalDate startDate = festival.getStartDate(); // 축제 시작인
        LocalDate currentDate = LocalDate.now(); // 유저 로컬 날짜

        Long dDayCount = ChronoUnit.DAYS.between(currentDate, startDate);
        String dDay;

        if("공연".equals(festival.getType().getType())) {
            if(dDayCount == 0) {
                dDay = "공연중";
            }
            else if (dDayCount > 0) {
                dDay = "D-"+ dDayCount;
            }
            else {
                dDay = "공연종료";
            }
        }
        else {
            if(dDayCount == 0) {
                dDay = "축제중";
            }
            else if (dDayCount > 0) {
                dDay = "D-"+ dDayCount;
            }
            else {
                dDay = "축제종료";
            }
        }
        return dDay;
    }


    // TODO 무한스크롤 구현중 ...
    // -> 일단은 요청 URL 파라미터로 지정하였음
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 8); // 걍 상수로 뺐음

    public List<FestivalPaginationResponseDTO> fetchFestivalPage(Long lastFestivalId,
                                                                 String sortBy,
                                                                 CategoryType category,
                                                                 RegionType region) {
        SortedType sortedType = SortedType.findBySortBy(sortBy); // sortBy 값을 SortedType로 변환

        Page<Festival> festivalPage = festivalRepository.findAllTogether(lastFestivalId, sortedType.name(), category, region, PAGE_REQUEST);
        List<Festival> festivalList = festivalPage.getContent();
        Integer totalCount = festivalList.size();

        return festivalList.stream()
                .filter(festival -> !festival.getIsDeleted())
                .map(festival -> new FestivalPaginationResponseDTO(festival, calculateDday(festival.getId()), totalCount))
                .collect(Collectors.toList());
    }
}
