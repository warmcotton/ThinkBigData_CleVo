package com.thinkbigdata.anna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserSentences")
@Getter @Setter
public class UserSentence {
    @Id @Column(name = "User_Sentence_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "Sentence_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Sentence sentence;
    @JoinColumn(name = "User_Record_id", nullable = false) @OneToOne(fetch = FetchType.LAZY)
    private UserRecord userRecord;

}
