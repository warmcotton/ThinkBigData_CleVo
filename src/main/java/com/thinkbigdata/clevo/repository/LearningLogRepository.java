package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.LearningLog;
import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningLogRepository extends JpaRepository<LearningLog, Integer> {
    Page<LearningLog> findByUser(User user, Pageable page);

    List<LearningLog> findBySentence(Sentence sentence);
}
