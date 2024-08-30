package com.thinkbigdata.clevo.dto;

import com.thinkbigdata.clevo.topic.TopicName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Getter @Setter
public class SentenceDto {
    private Integer id;
    private TopicName topic;
    private String eng;
    private String kor;
    private String base64;
    private Integer level;
}