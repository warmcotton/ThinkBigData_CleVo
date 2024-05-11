package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.UserSentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSentenceRepository extends JpaRepository<UserSentence, Integer> {
}
