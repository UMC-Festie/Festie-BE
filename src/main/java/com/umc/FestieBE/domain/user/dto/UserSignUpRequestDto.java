package com.umc.FestieBE.domain.user.dto;

import com.umc.FestieBE.domain.user.domain.Role;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.repository.config.RepositoryFragmentConfiguration;

import javax.validation.constraints.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignUpRequestDto {
    
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    private String checkPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, message = "닉네임이 너무 짧습니다.")
    private String nickname;

    @NotNull(message = "생년월일를 입력해주세요.")
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$")
    String birthday;

    private Character gender;
    @Builder
    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .birthday(LocalDate.parse(birthday))
                .gender(gender)
                .build();
    }
}
