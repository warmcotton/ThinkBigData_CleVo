package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.Topic;
import com.thinkbigdata.anna.topic.TopicName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByTopicName(TopicName topicName);
}
