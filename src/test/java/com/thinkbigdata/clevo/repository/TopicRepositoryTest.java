package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Topic;
import com.thinkbigdata.clevo.topic.TopicName;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
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
        topic.setTopicName(TopicName.TOPIC2);

        Topic savedTopic = topicRepository.save(topic);
        testEntityManager.flush();

        System.out.println(savedTopic.getId());
        System.out.println(savedTopic.getTopicName());
        assertNotNull(savedTopic);
        assertTrue(topic.getTopicName().equals(savedTopic.getTopicName()));
    }

    @Test
    void save_duplicate_topic_name() {
        Topic topic = new Topic();
        topic.setTopicName(TopicName.TOPIC1);
        topicRepository.save(topic);

        Topic newtopic = new Topic();
        newtopic.setTopicName(TopicName.TOPIC1);
        topicRepository.save(newtopic);
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.flush();
        });
    }

}