package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Topic;
import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.entity.UserImage;
import com.thinkbigdata.clevo.entity.UserTopic;
import com.thinkbigdata.clevo.role.Role;
import com.thinkbigdata.clevo.category.Category;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserTopicRepository userTopicRepository;
    @Autowired
    UserImageRepository userImageRepository;
    @Autowired
    TestEntityManager testEntityManager;
    void saveTopics() {
        for (Category category : Category.values()) {
            Topic topic = new Topic();
            topic.setCategory(category);
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
        user.setRole(Role.USER);
        user.setBirth(LocalDate.now());
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
        user.setRole(Role.USER);
        user.setBirth(LocalDate.now());
        user.setGender("M");

        userRepository.save(user);
        testEntityManager.flush();

        User newuser = new User();
        newuser.setEmail("test@test.com");
        newuser.setPassword("1111");
        newuser.setName("Name");
        newuser.setNickname("NickName");
        newuser.setRole(Role.USER);
        user.setBirth(LocalDate.now());
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
        user.setRole(Role.USER);
        user.setBirth(LocalDate.now());
        user.setGender("M");

        User savedUser = userRepository.save(user);
        testEntityManager.flush();

        Topic topic1 = topicRepository.findByCategory(Category.TOPIC1).get();
        Topic topic2 = topicRepository.findByCategory(Category.TOPIC2).get();

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

    @Test
    void save_with_default_image() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setRole(Role.USER);
        user.setBirth(LocalDate.now());
        user.setGender("M");
        User savedUser = userRepository.save(user);
        testEntityManager.flush();

        UserImage userImage = new UserImage();
        userImage.setUser(savedUser);
        userImageRepository.save(userImage);
        testEntityManager.flush();

        Optional<UserImage> dfImage = userImageRepository.findByUser(savedUser);
        UserImage savedImage = testEntityManager.refresh(dfImage.get());
        System.out.println(savedImage.getUser().getEmail());
        System.out.println(savedImage.getId());
        System.out.println(savedImage.getOriginName());
    }
}