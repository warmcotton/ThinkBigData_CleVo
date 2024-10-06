package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SentenceTopic")
@Getter @Setter
public class SentenceTopic {
    @Id
    @Column(name = "SentenceTopic_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "Sentence_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Sentence sentence;
    @JoinColumn(name = "Topic_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;
}
