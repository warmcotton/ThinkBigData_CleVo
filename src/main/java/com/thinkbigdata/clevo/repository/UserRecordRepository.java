package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRecordRepository extends JpaRepository<UserRecord, Integer> {
    Optional<UserRecord> findByName(String name);
}
