package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.enums.Role;
import com.thinkbigdata.clevo.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        for (Category category : Category.values()) {
            Topic topic = new Topic();
            topic.setCategory(category);
            topicRepository.save(topic);
        }
        testEntityManager.flush();
    }
    void saveSentence() {
        Topic topic = topicRepository.findByCategory(Category.HOBBY).get();
        Sentence sentence = new Sentence();
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

        UserSentence userSentence = new UserSentence();
        userSentence.setUser(user.get());
        userSentence.setSentence(sentence.get());
        userSentenceRepository.save(userSentence);

        System.out.println(userSentence.getUser().getEmail());
        System.out.println(userSentence.getSentence().getEng());
        assertNotNull(userSentence);
    }
}