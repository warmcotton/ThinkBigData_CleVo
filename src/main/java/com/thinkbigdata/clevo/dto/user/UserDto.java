package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.role.Role;
import com.thinkbigdata.clevo.topic.TopicName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Getter @Setter @Builder
public class UserDto {
    private String email;
    private String name;
    private String nickName;
    private LocalDate birth;
    private String gender;
    private Integer level;
    private Integer target;
    private Role role;
    private String imgPath;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;
    private List<TopicName> topic;
}
