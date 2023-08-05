package com.umc.FestieBE.domain.applicant_info.dto;

import com.umc.FestieBE.domain.applicant_info.domain.ApplicantInfo;
import com.umc.FestieBE.domain.temporary_user.TemporaryUser;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


public class ApplicantInfoRequestDTO {
    @Getter
    @NoArgsConstructor
    public static class BestieApplicationRequest {
        @NotNull(message = "같이가요 게시글 식별자는 필수 입력값입니다.")
        private Long togetherId;

        private String introduction;


        // DTO -> Entity
        public ApplicantInfo toEntity(User user, Together together){
            return ApplicantInfo.builder()
                    .user(user)
                    .together(together)
                    .introduction(introduction)
                    .isSelected(false)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BestieChoiceRequest {
        @NotNull(message = "같이가요 게시글 식별자는 필수 입력값입니다.")
        private Long togetherId;

        @NotEmpty(message = "Bestie로 선택할 사람(들)의 식별자 리스트는 필수 입력값입니다.")
        private List<Long> bestieList;
    }
}
