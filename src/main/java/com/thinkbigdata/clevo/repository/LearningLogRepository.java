package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.LearningLog;
import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningLogRepository extends JpaRepository<LearningLog, Integer> {
    List<LearningLog> findByUser(User user);

    List<LearningLog> findBySentence(Sentence sentence);
}
