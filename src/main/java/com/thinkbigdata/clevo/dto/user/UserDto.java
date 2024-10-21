package com.thinkbigdata.clevo.dto.user;

import com.thinkbigdata.clevo.enums.Role;
import com.thinkbigdata.clevo.enums.Category;
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
    private String nickname;
    private LocalDate birth;
    private String gender;
    private Integer level;
    private String difficulty;
    private Integer length;
    private Integer target;
    private Role role;
    private String img_path;
    private LocalDateTime created_date;
    private LocalDateTime lastLogin_date;
    private List<Category> category;
}
