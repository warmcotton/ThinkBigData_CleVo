package com.thinkbigdata.anna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "UserImages")
@Getter @Setter
@DynamicInsert
public class UserImage {
    @Id @Column(name = "User_Image_id") @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @JoinColumn(name = "User_id", nullable = false) @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(name = "User_Image_name", nullable = false) @ColumnDefault("'default-profile.png'")
    private String name;
    @Column(name = "User_Image_origin_name", nullable = false) @ColumnDefault("'default-profile.png'")
    private String originName;
    @Column(name = "User_Image_path", nullable = false) @ColumnDefault("'C:/anna/user/image/default-profile.png'")
    private String path;
}
