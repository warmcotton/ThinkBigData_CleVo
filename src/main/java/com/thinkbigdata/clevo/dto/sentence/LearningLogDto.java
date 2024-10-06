package com.thinkbigdata.clevo.dto.sentence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder @Getter @Setter
public class LearningLogDto {
    private Integer id;
    private String email;
    private Integer sentence_id;
    private String eng;
    private String kor;
    private Integer level;
    private Double accuracy;
    private Double fluency;
    private Double total_score;
    private LocalDateTime date;
}
