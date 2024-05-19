package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.topic.TopicName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter @Builder
public class UserUpdateDto {
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "올바른 닉네임을 입력하세요.(특수문자를 제외한 2~10자리 닉네임)")
    private String nickName;
    @Min(value = 1) @Max(value = 10)
    private Integer level;
    @Min(value = 1) @Max(value = 10)
    private Integer target;
    @Size(min = 3, max = 10)
    private List<TopicName> topic;
}
