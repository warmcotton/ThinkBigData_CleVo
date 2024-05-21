package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserRecords")
@Getter @Setter
public class UserRecord {
    @Id
    @Column(name = "User_Record_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "User_Record_name", nullable = false, unique = true)
    private String name;
    @Column(name = "User_Record_origin_name", nullable = false)
    private String originName;
    @Column(name = "User_Record_path", nullable = false)
    private String path;
}
