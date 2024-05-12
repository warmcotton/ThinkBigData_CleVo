package com.thinkbigdata.anna.dto;

import com.thinkbigdata.anna.role.Role;
import com.thinkbigdata.anna.topic.TopicName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter @Setter @Builder
public class UserDto {
    private String email;
    private String name;
    private String nickName;
    private Integer age;
    private String gender;
    private Integer level;
    private Integer target;
    private Role role;
    private String imgPath;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;
    private List<TopicName> topic;
}
