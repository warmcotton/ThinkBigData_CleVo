package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.entity.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
    Optional<Sentence> findByEng(String eng);
    List<Sentence> findAllByTopicInAndLevel(Iterable<Topic> topics, Integer level, Pageable pageable);
    @Query(value = "select s from Sentence s " +
            "where s.level = :level and s.topic = :topic ")
    List<Sentence> getRecommendSentences(Integer level, Topic topic, Pageable pageable);
}
