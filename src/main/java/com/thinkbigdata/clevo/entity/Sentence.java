package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Sentences")
@Getter @Setter
public class Sentence {
    @Id @Column(name = "Sentence_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JoinColumn(name = "Topic_id", nullable = false) @OneToOne(fetch = FetchType.LAZY)
    private Topic topic;
    @Column(name = "Sentence_eng", nullable = false)
    private String eng;
    @Column(name = "Sentence_kor", nullable = false)
    private String kor;
    @Column(name = "Sentence_level", nullable = false)
    private Integer level;
}
