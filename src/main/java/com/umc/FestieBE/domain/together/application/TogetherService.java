package com.umc.FestieBE.domain.together.application;

        import com.umc.FestieBE.domain.applicant_info.dao.ApplicantInfoRepository;
        import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
        import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoResponseDTO;
        import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
        import com.umc.FestieBE.domain.festival.domain.Festival;
        import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
        import com.umc.FestieBE.domain.temporary_user.TemporaryUserService;
        import com.umc.FestieBE.domain.together.dao.TogetherRepository;
        import com.umc.FestieBE.domain.together.domain.Together;
        import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
        import com.umc.FestieBE.domain.together.dto.TogetherResponseDTO;
        import com.umc.FestieBE.domain.user.domain.User;
        import com.umc.FestieBE.global.exception.CustomErrorCode;
        import com.umc.FestieBE.global.exception.CustomException;
        import com.umc.FestieBE.global.type.FestivalType;
        import com.umc.FestieBE.global.type.RegionType;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Service;

        import java.util.List;
        import java.util.stream.Collectors;

        import static com.umc.FestieBE.global.exception.CustomErrorCode.TOGETHER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TogetherService {

    private final TogetherRepository togetherRepository;
    private final FestivalRepository festivalRepository;

    private final TemporaryUserService temporaryUserService;
    private final ApplicantInfoRepository applicantInfoRepository;

    /**
     * 같이가요 게시글 등록
     */
    public void createTogether(TogetherRequestDTO request) {
        // 임시 유저
        TemporaryUser tempUser = temporaryUserService.createTemporaryUser();

        Together together;
        // festival 직접 입력할 경우
        if(request.getFestivalId() == null){
            FestivalType festivalType = FestivalType.findFestivalType(request.getFestivalType());
            RegionType region = RegionType.findRegionType(request.getRegion());
            //카테고리
            together = request.toEntity(tempUser, festivalType, region);
            // festival 정보 연동할 경우
        }else{
            Festival festival = festivalRepository.findById(request.getFestivalId())
                    .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
            together = request.toEntity(tempUser, festival);
        }
        togetherRepository.save(together);

    }

    /**
     * 같이가요 게시글 상세 조회
     */
    public TogetherResponseDTO getTogether(Long togetherId) {
        // 같이가요 게시글 조회
        Together together = togetherRepository.findByIdWithUser(togetherId)
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // 조회수 업데이트
        togetherRepository.updateView(togetherId);

        // 게시글 작성자 조회
        // TODO isWriter 확인
        TemporaryUser writer = together.getTemporaryUser(); //임시 유저 사용
        // 로그인 한 사용자 - isWriter?

        // TODO Bestie 신청 내역 & 신청 여부 & 매칭 성공 여부
        // 신청 내역
        List<ApplicantInfo> applicantInfoList = applicantInfoRepository.findByTogetherIdWithUser(togetherId);
        List<ApplicantInfoResponseDTO> applicantList = applicantInfoList.stream()
                .map(applicantInfo -> ApplicantInfoResponseDTO.toDTO(applicantInfo))
                .collect(Collectors.toList());
        // 로그인 한 사용자 - Bestie 신청? 매칭 성공?


        // festival 정보 및 연동 여부
        boolean isLinked = (together.getFestival() != null);

    }
}

