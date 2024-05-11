package com.thinkbigdata.anna.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "UserTopic")
public class UserTopic {
    @Id @Column(name = "UserTopic_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user_id;
    @JoinColumn(name = "Topic_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic_id;
}
