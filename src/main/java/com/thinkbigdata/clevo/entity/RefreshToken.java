package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "RefreshToken")
@Getter @Setter
public class RefreshToken {
    @Id @Column(name = "RefreshToken_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "RefreshToken_value", nullable = false)
    private String value;
    @Column(name = "RefreshToken_user_email", nullable = false)
    private String email;
    @Column(name = "RefreshToken_expired_date")
    private LocalDateTime expiredDate;
}
