package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.*;
import com.thinkbigdata.anna.topic.TopicName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
class UserSentenceRepositoryTest {
    @Autowired
    UserSentenceRepository userSentenceRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    SentenceRepository sentenceRepository;
    @Autowired
    UserRecordRepository userRecordRepository;
    @Autowired
    TestEntityManager testEntityManager;
    void saveUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setAge(20);
        user.setGender("M");
        userRepository.save(user);
        testEntityManager.flush();
    }
    void saveUserRecord() {
        UserRecord userRecord = new UserRecord();
        userRecord.setName("random_salt.wav");
        userRecord.setOrigin_name("original_name.wav");
        userRecord.setPath("C:/anna/record/wav");
        userRecordRepository.save(userRecord);
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
        saveUserRecord();
    }

    @Test
    void save() {
        Optional<User> user = userRepository.findByEmail("test@test.com");
        Optional<Sentence> sentence = sentenceRepository.findById(1);
        Optional<UserRecord> userRecord = userRecordRepository.findById(1);

        UserSentence userSentence = new UserSentence();
        userSentence.setUser(user.get());
        userSentence.setSentence(sentence.get());
        userSentence.setUserRecord(userRecord.get());
        UserSentence savedUserSentence = userSentenceRepository.save(userSentence);

        System.out.println(savedUserSentence.getUser().getEmail());
        System.out.println(savedUserSentence.getSentence().getEng());
        System.out.println(savedUserSentence.getUserRecord().getOrigin_name());
        assertNotNull(savedUserSentence);
        assertTrue(userSentence.getId().equals(savedUserSentence.getId()));
    }
}