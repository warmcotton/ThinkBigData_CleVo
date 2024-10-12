package com.thinkbigdata.clevo.dto.sentence;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter @Setter
public class UserSentenceDto {
    @NotNull @Min(value = 1)
    private Integer sentence_id;
    @NotBlank
    private String base64;
    private SentenceDto sentence;
    private List<LearningLogDto> logs;
    private LocalDateTime date;
}
