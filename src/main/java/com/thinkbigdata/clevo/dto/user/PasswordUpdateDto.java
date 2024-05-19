package com.thinkbigdata.clevo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordUpdateDto {
    @NotBlank(message = "기존 비밀번호를 입력하세요.")
    private String exPassword;
    @NotBlank(message = "새로운 비밀번호를 입력하세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "올바른 비밀번호를 입력하세요.(대 소문자, 숫자, 특수문자 포함 8-16자리)")
    private String newPassword1;
    @NotBlank(message = "새로운 비밀번호를 입력하세요.")
    private String newPassword2;
}
