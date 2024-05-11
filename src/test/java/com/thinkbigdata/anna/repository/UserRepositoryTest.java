package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void save() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setAge(20);
        user.setGender("M");

        User savedUser = userRepository.save(user);

        System.out.println(savedUser.getId());
        System.out.println(savedUser.getDate());
        assertNotNull(savedUser);
        assertTrue(user.getId().equals(savedUser.getId()));
        assertTrue(user.getPassword().equals(savedUser.getPassword()));
    }
}