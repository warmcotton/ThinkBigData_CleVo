package com.thinkbigdata.clevo.dto.sentence;

import com.thinkbigdata.clevo.enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Builder
public class SentenceDto {
    private Integer id;
    @NotBlank
    private String eng;
    @NotBlank
    private String kor;
    @NotBlank
    private String base64;
    private List<Category> categories;
    private Integer level;
}