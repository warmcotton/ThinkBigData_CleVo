package com.thinkbigdata.clevo.dto;

import com.thinkbigdata.clevo.dto.user.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder @Getter @Setter
public class LearningLogDto {
    private Integer id;
    private String user;
    private Integer sentence;
    private String path;
    private Integer clarity;
    private Integer fluency;
    private Integer average;
    private LocalDateTime time;
}
