package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Integer> {
    Optional<UserImage> findByUser(User user);
}
