package com.thinkbigdata.anna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserRecords")
@Getter @Setter
public class UserRecord {
    @Id
    @Column(name = "User_Record_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "User_Record_name")
    private String name;
    @Column(name = "User_Record_origin_name")
    private String origin_name;
    @Column(name = "User_Record_path")
    private String path;
}
