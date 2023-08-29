package com.umc.FestieBE.domain.applicant_info.application;

import com.umc.FestieBE.domain.applicant_info.dao.ApplicantInfoRepository;
import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoBestieListDTO;
import com.umc.FestieBE.domain.applicant_info.dto.ApplicantInfoRequestDTO;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.BestieResponseDTO;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class ApplicantInfoService {

    private final UserRepository userRepository;
    private final TogetherRepository togetherRepository;
    private final ApplicantInfoRepository applicantInfoRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 같이가요 Bestie 신청
     */
    public void createBestieApplication(ApplicantInfoRequestDTO.BestieApplicationRequest request) {
        // 같이가요 게시글 조회
        Together together = togetherRepository.findByIdWithUser(request.getTogetherId())
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // Bestie 신청 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() == together.getUser().getId()){ //자신이 작성한 게시글일 경우
            throw new CustomException(SELF_APPLICATION_NOT_ALLOWED);
        }

        ApplicantInfo applicantInfo = request.toEntity(user, together);

        // Bestie 등록
        // 매칭 대기 중
        if(together.getStatus() == 0) {
            // 신청 내역이 존재하는지 확인
            applicantInfoRepository.findByTogetherIdAndUserId(request.getTogetherId(), user.getId())
                    .ifPresent(findApplicantInfo -> {
                        throw new CustomException(APPLICANT_INFO_ALREADY_EXISTS);
                    });
            applicantInfoRepository.save(applicantInfo);
        }
        // 매칭 완료
        else{
            throw new CustomException(MATCHING_ALREADY_COMPLETED);
        }
    }


    /**
     * 같이가요 Bestie 선택
     */
    @Transactional
    public void createBestieChoice(ApplicantInfoRequestDTO.BestieChoiceRequest request){
        // 같이가요 게시글 조회
        Together together = togetherRepository.findById(request.getTogetherId())
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // Bestie 선택 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != together.getUser().getId()){ //자신이 작성한 게시글이 아닐 경우
            throw new CustomException(BESTIE_SELECTION_NOT_ALLOWED);
        }

        // Bestie 선택 반영
        if(together.getStatus() == 0) {

            // bestieList 검증
            List<Long> bestieIdList = request.getBestieList();

            List<ApplicantInfo> applicantInfoList = applicantInfoRepository.findByTogether(together);
            if(applicantInfoList.isEmpty()){ //Bestie 신청 내역이 존재하지 않을 경우
                throw new CustomException(APPLICANT_INFO_NOT_FOUND);
            }
            List<Long> applicantIdList = applicantInfoList.stream()
                    .map(applicantInfo -> applicantInfo.getUser().getId())
                    .collect(Collectors.toList());

            if(applicantIdList.containsAll(bestieIdList)){
                applicantInfoRepository.updateStatus(together.getId(), bestieIdList);
            }else{ //Bestie 선택 내역에 Bestie 신청을 하지 않은 사용자가 존재할 경우
                throw new CustomException(INCONSISTENT_APPLICANT_INFO);
            }

            // 같이가요 매칭 상태 변경
            togetherRepository.updateStatusMatched(together.getId());
        }else{
            throw new CustomException(MATCHING_ALREADY_COMPLETED);
        }
    }


    /** 같이가요 매칭이력 */
    public ApplicantInfoBestieListDTO fetchRecentApplicantInfo(User user) {
        List<ApplicantInfo> applicantInfoList = applicantInfoRepository.findTop8ByUserIdOrderByCreatedAtDesc(user.getId());

        List<BestieResponseDTO> data = applicantInfoList.stream()
                .map(applicantInfo -> getBestieResponseDTO(applicantInfo))
                .collect(Collectors.toList());

        long totalCount = data.size();

        return new ApplicantInfoBestieListDTO(data, totalCount);
    }

    private BestieResponseDTO getBestieResponseDTO(ApplicantInfo applicantInfo) {
        Together together = togetherRepository.findById(applicantInfo.getTogether().getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, "Bestie를 신청한 같이가요 게시글을 찾을 수 없습니다."));

        Boolean status = applicantInfoRepository.findStatusByTogetherIdAndUserId(together.getId(), applicantInfo.getUser().getId());

        String isApplicationSuccess;
        if (together.getStatus() == 1 && status) {
            isApplicationSuccess = "매칭성공";
        } else if (together.getStatus() == 1 && !status){
            isApplicationSuccess = "매칭실패";
        } else {
            isApplicationSuccess = "매칭중";
        }

        return new BestieResponseDTO(together, isApplicationSuccess);
    }

}