package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.User;
import com.thinkbigdata.anna.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Integer> {
    Optional<UserImage> findByUser(User user);
}
