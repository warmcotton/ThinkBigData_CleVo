package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.Topic;
import com.thinkbigdata.anna.entity.User;
import com.thinkbigdata.anna.entity.UserTopic;
import com.thinkbigdata.anna.topic.TopicName;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
@EnableJpaAuditing
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserTopicRepository userTopicRepository;
    @Autowired
    TestEntityManager testEntityManager;
    void saveTopics() {
        for (TopicName topicName: TopicName.values()) {
            Topic topic = new Topic();
            topic.setTopicName(topicName);
            topicRepository.save(topic);
        }
    }
    @BeforeEach
    void contextLoads() {
        saveTopics();
    }

    @Test
    void save() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setAge(20);
        user.setGender("M");

        User savedUser = userRepository.save(user);
        testEntityManager.flush();

        System.out.println(savedUser.getId());
        System.out.println(savedUser.getDate());
        assertNotNull(savedUser);
        assertTrue(user.getId().equals(savedUser.getId()));
        assertTrue(user.getPassword().equals(savedUser.getPassword()));
    }

    @Test
    void save_duplicate_user_email() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setAge(20);
        user.setGender("M");

        userRepository.save(user);
        testEntityManager.flush();

        User newuser = new User();
        newuser.setEmail("test@test.com");
        newuser.setPassword("1111");
        newuser.setName("Name");
        newuser.setNickname("NickName");
        newuser.setAge(20);
        newuser.setGender("M");

        userRepository.save(newuser);
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.flush();
        });
    }

    @Test
    void save_with_topic() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setAge(20);
        user.setGender("M");

        User savedUser = userRepository.save(user);
        testEntityManager.flush();

        Topic topic1 = topicRepository.findByTopicName(TopicName.TOPIC1).get();
        Topic topic2 = topicRepository.findByTopicName(TopicName.TOPIC2).get();

        UserTopic userTopic1 = new UserTopic();
        userTopic1.setUser(savedUser);
        userTopic1.setTopic(topic1);

        UserTopic userTopic2 = new UserTopic();
        userTopic2.setUser(savedUser);
        userTopic2.setTopic(topic2);

        List<UserTopic> userTopics = asList(userTopic1, userTopic2);
        userTopicRepository.saveAll(userTopics);
        testEntityManager.flush();

        List<UserTopic> savedUserTopics = userTopicRepository.findByUser(savedUser);

        assertArrayEquals(savedUserTopics.toArray(), userTopics.toArray());
    }
}