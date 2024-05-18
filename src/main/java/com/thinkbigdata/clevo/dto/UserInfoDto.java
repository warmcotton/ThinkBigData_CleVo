package com.thinkbigdata.clevo.dto;

import com.thinkbigdata.clevo.topic.TopicName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserInfoDto {
    @NotBlank(message = "이메일 필드 오류")
    private String email;
    @Min(value = 1) @Max(value = 10)
    private Integer level;
    @Min(value = 1) @Max(value = 10)
    private Integer target;
    @Size(min = 3, max = 10)
    private List<TopicName> topic;
}
