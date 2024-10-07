package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Topic;
import com.thinkbigdata.clevo.enums.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
class TopicRepositoryTest {
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void save() {
        Topic topic = new Topic();
        topic.setCategory(Category.TOPIC2);

        Topic savedTopic = topicRepository.save(topic);
        testEntityManager.flush();

        System.out.println(savedTopic.getId());
        System.out.println(savedTopic.getCategory());
        assertNotNull(savedTopic);
        assertTrue(topic.getCategory().equals(savedTopic.getCategory()));
    }

    @Test
    void save_duplicate_topic_name() {
        Topic topic = new Topic();
        topic.setCategory(Category.TOPIC1);
        topicRepository.save(topic);

        Topic newtopic = new Topic();
        newtopic.setCategory(Category.TOPIC1);
        assertThrows(DataIntegrityViolationException.class, () -> {
            topicRepository.save(newtopic);
        });
    }
}