package com.thinkbigdata.clevo.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter @Setter @Builder
public class PostDto {
    private Integer id;
    @NotBlank(message = "제목을 입력하세요.")
    private String title;
    private String content;
    private String writer;
    private Integer views;
    private LocalDateTime created;
    private LocalDateTime modified;
    private List<CommentDto> comments;
}
