package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Topic;
import com.thinkbigdata.clevo.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByCategory(Category category);
}
