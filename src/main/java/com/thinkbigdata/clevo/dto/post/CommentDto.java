package com.thinkbigdata.clevo.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter @Builder
public class CommentDto {
    private Integer id;
    private Integer post_id;
    @NotBlank(message = "내용을 입력하세요.")
    private String content;
    private String writer;
    private LocalDateTime date;
}
