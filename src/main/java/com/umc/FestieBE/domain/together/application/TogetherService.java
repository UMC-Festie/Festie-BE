package com.umc.FestieBE.domain.together.application;

import com.umc.FestieBE.domain.applicant_info.dao.ApplicantInfoRepository;
import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoResponseDTO;
import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkResponseDTO;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserRepository;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import com.umc.FestieBE.global.type.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class TogetherService {

    private final TogetherRepository togetherRepository;
    private final FestivalRepository festivalRepository;
    private final TemporaryUserService temporaryUserService;
    private final ApplicantInfoRepository applicantInfoRepository;
    private final TemporaryUserRepository temporaryUserRepository;

    /**
     * 같이가요 게시글 등록
     */
    public void createTogether(TogetherRequestDTO.TogetherRequest request) {
        // 임시 유저
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();
        TemporaryUser tempUser2 = temporaryUserService.createTemporaryUser2(); // kim

        // 공연/축제 정보 연동 시 DB 에서 확인
        if(request.getFestivalId() != null){
            festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
        }

        // 같이가요 게시글 등록
        FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
        //CategoryType categoryType = null; //카테고리
        RegionType regionType = RegionType.findRegionType(request.getRegion());
        //Together together = request.toEntity(tempUser, festivalType, categoryType, regionType);
        Together together = request.toEntity(tempUser, festivalType, request.getCategory(), regionType);
        togetherRepository.save(together);
    }


    /**
     * 같이가요 게시글 상세 조회
     */
    public TogetherResponseDTO.TogetherDetailResponse getTogether(Long togetherId) {
        // 조회수 업데이트
        togetherRepository.updateView(togetherId);

        // 같이가요 게시글 조회
        Together together = togetherRepository.findByIdWithUser(togetherId)
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 게시글 작성자 조회
        // TODO isWriter 확인
        // 로그인 한 사용자 - isWriter?

        // TODO Bestie 신청 내역 & 신청 여부 & 매칭 성공 여부
        // 신청 내역
        List<ApplicantInfo> applicantInfoList = applicantInfoRepository.findByTogetherIdWithUser(togetherId);
        List<ApplicantInfoResponseDTO> applicantList = applicantInfoList.stream()
                .map(ApplicantInfoResponseDTO::new)
                .collect(Collectors.toList());
        // 로그인 한 사용자 - Bestie 신청? 매칭 성공?
        // 임시
        Boolean isWriter = null;
        Boolean isApplicant = null;
        Boolean isApplicationSuccess = null;

        // festival 정보 및 연동 여부
        boolean isLinked = false;
        boolean isDeleted = false;
        FestivalLinkResponseDTO festivalInfo;

        // 공연/축제 연동 O
        if (together.getFestivalId() != null) {
            isLinked = true;
            Festival linkedFestival = festivalRepository.findById(together.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            //삭제 되었을 경우
            if(linkedFestival.getIsDeleted()){ isDeleted = true; }
            festivalInfo = new FestivalLinkResponseDTO(linkedFestival);
        }
        // 공연/축제 연동 X (직접 입력)
        else {
            festivalInfo = new FestivalLinkResponseDTO(together);
        }

        return new TogetherResponseDTO.TogetherDetailResponse(together, applicantList, isLinked, isDeleted, festivalInfo,
                isWriter, isApplicant, isApplicationSuccess);

    }


    /**
     * 같이가요 게시글 수정
     */
    @Transactional
    public void updateTogether(Long togetherId, TogetherRequestDTO.TogetherRequest request){

        // 같이가요 게시글 조회
        Together together = togetherRepository.findById(togetherId)
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 게시글 수정 권한 확인

        // 게시글 수정 반영
        together.updateTogether(request);
    }


    /**
     * 같이가요 게시글 삭제
     */
    public void deleteTogether(Long togetherId){
        // 같이가요 게시글 조회
        togetherRepository.findById(togetherId)
            .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 삭제하려는 유저가 게시글 작성자인지 확인

        // Bestie 신청 내역 삭제
        applicantInfoRepository.deleteByTogetherId(togetherId);

        // 같이가요 게시글 삭제
        togetherRepository.deleteById(togetherId);
    }


    /**
     * 같이가요 게시글 목록 조회
     */
    public TogetherResponseDTO.TogetherListResponse getTogetherList
        (int page,
         Integer type, Integer category, String region, Integer status, Integer sort){
        // 페이징
        PageRequest pageRequest = PageRequest.of(page, 3);

        // ENUM 타입 (festivalType, regionType, categoryType)
        String festivalType = null;
        if(type != null){
            festivalType = FestivalType.findFestivalType(type).name();
        }
        String regionType = null;
        if(region != null){
            regionType = RegionType.findRegionType(region).name();
        }
        //CategoryType categoryType = null; //카테고리

        Slice<Together> result = togetherRepository.findAllTogether(pageRequest, festivalType, category, regionType, status, String.valueOf(sort));
        List<TogetherResponseDTO.TogetherListDetailResponse> data = result.stream()
                .map(together -> new TogetherResponseDTO.TogetherListDetailResponse(together))
                .collect(Collectors.toList());

        PageRequest countPageRequest = PageRequest.of(0, 3);
        long totalCount = togetherRepository.countTogether(countPageRequest, festivalType, category, regionType, status, String.valueOf(sort));
        int pageNum = result.getNumber();
        boolean hasNext = result.hasNext();
        boolean hasPrevious = result.hasPrevious();

        return new TogetherResponseDTO.TogetherListResponse(data, totalCount, pageNum, hasNext, hasPrevious);
    }
}


