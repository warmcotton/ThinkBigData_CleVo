package com.thinkbigdata.anna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.thinkbigdata.anna.topic.TopicName;
@Entity
@Table(name = "Topic")
@Getter @Setter
public class Topic {
    @Id @Column(name = "Topic_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "Topic_name", unique = true, nullable = false) @Enumerated(EnumType.STRING)
    private TopicName topicName;
}
