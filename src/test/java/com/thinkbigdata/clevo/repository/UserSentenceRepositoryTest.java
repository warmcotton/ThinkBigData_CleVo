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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
@EnableJpaAuditing
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
        user.setRole(Role.USER);
        user.setBirth(LocalDate.now());
        user.setGender("M");
        userRepository.save(user);
        testEntityManager.flush();
    }
    void saveUserRecord() {
        UserRecord userRecord = new UserRecord();
        userRecord.setName("random_salt.wav");
        userRecord.setOriginName("original_name.wav");
        userRecord.setPath("C:/clevo/record/wav");
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
        Optional<Sentence> sentence = sentenceRepository.findByEng("test Sentence");
        Optional<UserRecord> userRecord = userRecordRepository.findByName("random_salt.wav");

        UserSentence userSentence = new UserSentence();
        userSentence.setUser(user.get());
        userSentence.setSentence(sentence.get());
        userSentence.setUserRecord(userRecord.get());
        userSentence.setClarity(5);
        userSentence.setFluency(3);
        userSentence.setTotalScore(8);
        UserSentence savedUserSentence = userSentenceRepository.save(userSentence);

        System.out.println(savedUserSentence.getUser().getEmail());
        System.out.println(savedUserSentence.getSentence().getEng());
        System.out.println(savedUserSentence.getUserRecord().getOriginName());
        System.out.println(savedUserSentence.getCreatedDate());
        System.out.println(savedUserSentence.getModifiedDate());
        assertNotNull(savedUserSentence);
        assertTrue(userSentence.getId().equals(savedUserSentence.getId()));
    }

    @Test
    void save_and_modify() {
        Optional<User> user = userRepository.findByEmail("test@test.com");
        Optional<Sentence> sentence = sentenceRepository.findByEng("test Sentence");
        Optional<UserRecord> userRecord = userRecordRepository.findByName("random_salt.wav");

        UserSentence userSentence = new UserSentence();
        userSentence.setUser(user.get());
        userSentence.setSentence(sentence.get());
        userSentence.setUserRecord(userRecord.get());
        userSentence.setClarity(5);
        userSentence.setFluency(3);
        userSentence.setTotalScore(8);
        userSentenceRepository.save(userSentence);
        testEntityManager.flush();

        UserSentence savedUserSentence = userSentenceRepository.findById(userSentence.getId()).get();
        savedUserSentence.setClarity(6);
        savedUserSentence.setFluency(4);
        savedUserSentence.setTotalScore(10);
        UserSentence saved = userSentenceRepository.save(savedUserSentence);
        testEntityManager.flush();

        System.out.println(saved.getCreatedDate());
        System.out.println(saved.getModifiedDate());
        assertNotNull(savedUserSentence);
        assertNotEquals(Timestamp.valueOf(saved.getCreatedDate()).getTime(), Timestamp.valueOf(saved.getModifiedDate()).getTime());
    }
}