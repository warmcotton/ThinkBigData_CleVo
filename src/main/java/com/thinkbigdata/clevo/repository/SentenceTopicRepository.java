package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.entity.SentenceTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceTopicRepository extends JpaRepository<SentenceTopic, Integer> {
    List<SentenceTopic> findBySentence(Sentence sentence);
}
