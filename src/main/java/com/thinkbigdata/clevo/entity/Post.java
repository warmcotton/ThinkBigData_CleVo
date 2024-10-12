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
@Table(name = "Posts")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class Post {
    @Id
    @Column(name = "Post_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Post_title", nullable = false)
    private String title;
    @Column(name = "Post_content")
    private String content;
    @Column(name = "Post_views")
    private Integer views;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(name = "Post_created_date") @CreatedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @Column(name = "Post_modified_date") @LastModifiedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;
}
