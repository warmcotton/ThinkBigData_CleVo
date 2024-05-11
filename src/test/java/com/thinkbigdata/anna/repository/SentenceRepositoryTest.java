package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.Sentence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SentenceRepositoryTest {
    @Autowired
    SentenceRepository sentenceRepository;

    @Test
    void save() {
        Sentence sentence = new Sentence();
        sentence.setEng("test Sentence");
        sentence.setKor("테스트 문장");
        sentence.setLevel(10);

        Sentence savedSentence = sentenceRepository.save(sentence);

        System.out.println(savedSentence.getId());
        System.out.println(savedSentence.getEng());
        System.out.println(savedSentence.getKor());
        assertNotNull(savedSentence);
        assertTrue(sentence.getId().equals(savedSentence.getId()));
        assertTrue(sentence.getKor().equals(savedSentence.getKor()));
    }
}