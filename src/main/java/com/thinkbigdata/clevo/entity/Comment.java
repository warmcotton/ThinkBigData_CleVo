package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "Comments")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class Comment {
    @Id
    @Column(name = "Comment_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Comment_content", nullable = false)
    private String content;
    @JoinColumn(name = "Post_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(name = "Comment_created_date") @CreatedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
