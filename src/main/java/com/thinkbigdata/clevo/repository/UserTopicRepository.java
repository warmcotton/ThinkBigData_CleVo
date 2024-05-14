package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.entity.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTopicRepository extends JpaRepository<UserTopic, Integer> {
    List<UserTopic> findByUser(User user);
}
