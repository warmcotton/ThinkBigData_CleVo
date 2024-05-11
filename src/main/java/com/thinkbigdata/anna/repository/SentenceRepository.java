package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
    Optional<Sentence> findByEng(String eng);
}
