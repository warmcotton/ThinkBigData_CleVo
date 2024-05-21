package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "LearningLogs")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class LearningLog {
    @Id @Column(name = "Learning_log_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "Sentence_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Sentence sentence;
    @JoinColumn(name = "User_Record_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private UserRecord record;
    @Column(name = "Learning_log_clarity", nullable = false)
    private Integer clarity;
    @Column(name = "Learning_log_fluency", nullable = false)
    private Integer fluency;
    @Column(name = "Learning_log_total_score", nullable = false)
    private Integer totalScore;
    @CreatedDate @Column(name = "Learning_log_date", nullable = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
}
