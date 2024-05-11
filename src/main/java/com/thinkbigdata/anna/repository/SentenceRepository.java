package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
}
