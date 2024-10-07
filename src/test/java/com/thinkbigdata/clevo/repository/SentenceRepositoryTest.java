package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.entity.Topic;
import com.thinkbigdata.clevo.enums.Category;
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
public class SentenceRepositoryTest {
    @Autowired
    SentenceRepository sentenceRepository;
    @Autowired
    TopicRepository topicRepository;
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
        testEntityManager.flush();
    }

    @Test
    void save() {
        Optional<Topic> topic = topicRepository.findByCategory(Category.TOPIC1);
        Sentence sentence = new Sentence();

        sentence.setEng("test Sentence");
        sentence.setKor("테스트 문장");
        sentence.setLevel(10);

        Sentence savedSentence = sentenceRepository.save(sentence);
        testEntityManager.flush();

        System.out.println(savedSentence.getId());
        System.out.println(savedSentence.getEng());
        System.out.println(savedSentence.getKor());
        assertNotNull(savedSentence);
        assertTrue(sentence.getId().equals(savedSentence.getId()));
        assertTrue(sentence.getKor().equals(savedSentence.getKor()));
    }
}