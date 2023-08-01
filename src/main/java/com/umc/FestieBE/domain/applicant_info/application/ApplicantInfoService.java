package com.umc.FestieBE.domain.applicant_info.application;

import com.umc.FestieBE.domain.applicant_info.dao.ApplicantInfoRepository;
import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.temporary_user.TemporaryUserRepository;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class ApplicantInfoService {

    private final TemporaryUserRepository temporaryUserRepository;
    private final TogetherRepository togetherRepository;
    private final ApplicantInfoRepository applicantInfoRepository;

    /**
     * 같이가요 Bestie 신청
     */
    public void createBestieApplication(TogetherRequestDTO.BestieApplicationRequest request) {
        // 임시 유저
        TemporaryUser tempUser = temporaryUserRepository.findById(2L).get();

        // 같이가요 게시글 조회
        Together together = togetherRepository.findByIdWithUser(request.getTogetherId())
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        ApplicantInfo applicantInfo = request.toEntity(tempUser, together);

        // Bestie 등록
        // 매칭 대기 중
        if(together.getStatus() == 0) {
            // 신청 내역이 존재하는지 확인
            applicantInfoRepository.findByTogetherIdAndUserId(request.getTogetherId(), tempUser.getId())
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
    public void createBestieChoice(TogetherRequestDTO.BestieChoiceRequest request){
        // 같이가요 게시글 조회
        Together together = togetherRepository.findById(request.getTogetherId())
                .orElseThrow(() -> new CustomException(TOGETHER_NOT_FOUND));

        // Bestie 선택 반영
        if(together.getStatus() == 0) {
            List<Long> bestieIdList = request.getBestieList();
            applicantInfoRepository.updateStatus(together.getId(), bestieIdList);

            // 같이가요 매칭 상태 변경
            togetherRepository.updateStatusMatched(together.getId());
        }else{
            throw new CustomException(MATCHING_ALREADY_COMPLETED);
        }
    }

}
