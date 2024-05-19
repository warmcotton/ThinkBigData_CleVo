package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.topic.TopicName;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserInfoDto {
    @NotNull
    @Min(value = 1) @Max(value = 10)
    private Integer level;
    @NotNull
    @Min(value = 1) @Max(value = 10)
    private Integer target;
    @NotNull
    @Size(min = 3, max = 10)
    private List<TopicName> topic;
}
