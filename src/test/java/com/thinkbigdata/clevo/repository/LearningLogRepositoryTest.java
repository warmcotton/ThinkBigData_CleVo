package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.role.Role;
import com.thinkbigdata.clevo.topic.TopicName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
class LearningLogRepositoryTest {
    @Autowired
    LearningLogRepository learningLogRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    SentenceRepository sentenceRepository;
    @Autowired
    TestEntityManager testEntityManager;
    void saveUser() {
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
    }

    void saveTopics() {
        for (TopicName topicName: TopicName.values()) {
            Topic topic = new Topic();
            topic.setTopicName(topicName);
            topicRepository.save(topic);
        }
        testEntityManager.flush();
    }
    void saveSentence() {
        Topic topic = topicRepository.findByTopicName(TopicName.TOPIC1).get();
        Sentence sentence = new Sentence();
        sentence.setTopic(topic);
        sentence.setEng("test Sentence");
        sentence.setKor("테스트 문장");
        sentence.setLevel(10);
        sentenceRepository.save(sentence);
    }
    @BeforeEach
    void contextLoads() {
        saveUser();
        saveTopics();
        saveSentence();
    }

    @Test
    void save() {
        Optional<User> user = userRepository.findByEmail("test@test.com");
        Optional<Sentence> sentence = sentenceRepository.findByEng("test Sentence");

        LearningLog learningLog = new LearningLog();
        learningLog.setUser(user.get());
        learningLog.setSentence(sentence.get());
        learningLog.setClarity(5.0F);
        learningLog.setFluency(3.0F);
        learningLog.setTotalScore(8.0F);
        LearningLog savedLog = learningLogRepository.save(learningLog);

        System.out.println(savedLog.getUser().getEmail());
        System.out.println(savedLog.getSentence().getEng());
        System.out.println(savedLog.getDate());
        assertNotNull(savedLog);
        assertTrue(learningLog.getId().equals(savedLog.getId()));
    }
}