package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRecordRepository extends JpaRepository<UserRecord, Integer> {
    Optional<UserRecord> findByName(String name);
}
