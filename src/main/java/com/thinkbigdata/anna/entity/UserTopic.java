package com.thinkbigdata.anna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserTopic")
@Getter @Setter
public class UserTopic {
    @Id @Column(name = "UserTopic_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JoinColumn(name = "User_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "Topic_id", nullable = false) @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;
}
