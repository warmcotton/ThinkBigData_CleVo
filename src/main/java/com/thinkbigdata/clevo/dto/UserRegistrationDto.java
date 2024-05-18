package com.thinkbigdata.clevo.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UserRegistrationDto {
    @NotBlank(message = "이메일은 필수 입력값 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호 형식이 올바르지 않습니다. (대 소문자, 숫자, 특수문자 포함 8-16자리)")
    private String password1;
    private String password2;
    @NotBlank(message = "이름은 필수로 입력해야 합니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "특수문자를 제외한 2~10자리 이름")
    private String name;
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "특수문자를 제외한 2~10자리 닉네임")
    private String nickName;
    @NotNull(message = "생년월일은 필수로 입력해야 합니다.")
    private LocalDate birth;
    @NotBlank(message = "성별은 필수로 입력해야 합니다.")
    @Pattern(regexp = "^[MF]$")
    private String gender;
}
