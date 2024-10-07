package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserSentences")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class UserSentence {
    @Id @Column(name = "User_Sentence_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "Sentence_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Sentence sentence;
}
