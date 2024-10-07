package com.thinkbigdata.clevo.dto.user;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UserRegistrationDto {
    @NotBlank(message = "이메일을 입력하세요.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "올바른 이메일을 입력하세요.")
    private String email;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "올바른 비밀번호를 입력하세요.(대 소문자, 숫자, 특수문자 포함 8-16자리)")
    private String password1;
    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password2;
    @NotBlank(message = "이름을 입력하세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣]{2,5}$", message = "올바른 이름을 입력하세요.")
    private String name;
    @NotBlank(message = "닉네임을 입력하세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "올바른 닉네임을 입력하세요.(특수문자를 제외한 2~10자리 닉네임)")
    private String nickname;
    @NotNull(message = "생년월일을 입력하세요.")
    private LocalDate birth;
    @NotBlank(message = "성별을 입력하세요.")
    @Pattern(regexp = "^[MF]$")
    private String gender;
}
