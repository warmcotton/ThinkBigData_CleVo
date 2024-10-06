package com.thinkbigdata.clevo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.thinkbigdata.clevo.category.Category;
@Entity
@Table(name = "Topics")
@Getter @Setter
public class Topic {
    @Id @Column(name = "Topic_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Topic_category", unique = true, nullable = false) @Enumerated(EnumType.STRING)
    private Category category;
}
