package com.thinkbigdata.clevo.dto.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class UserProfileUpdateDto {
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "올바른 닉네임을 입력하세요.(특수문자를 제외한 2~10자리 닉네임)")
    private String nickname;
    private String ex_password;
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "올바른 비밀번호를 입력하세요.(대 소문자, 숫자, 특수문자 포함 8-16자리)")
    private String new_password1;
    private String new_password2;
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "올바른 이메일을 입력하세요.")
    private String email;
}
