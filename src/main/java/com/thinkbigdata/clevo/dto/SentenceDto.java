package com.thinkbigdata.clevo.dto;

import com.thinkbigdata.clevo.topic.TopicName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Getter @Setter
public class SentenceDto {
    private Integer id;
    private TopicName topic;
    private String eng;
    private String kor;
    private Integer level;
}
