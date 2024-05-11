package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.User;
import com.thinkbigdata.anna.entity.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTopicRepository extends JpaRepository<UserTopic, Integer> {
    List<UserTopic> findByUser(User user);
}
