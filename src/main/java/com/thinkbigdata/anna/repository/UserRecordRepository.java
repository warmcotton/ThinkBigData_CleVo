package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRecordRepository extends JpaRepository<UserRecord, Integer> {
}
