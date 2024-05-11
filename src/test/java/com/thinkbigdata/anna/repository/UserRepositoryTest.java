package com.thinkbigdata.anna.repository;

import com.thinkbigdata.anna.entity.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@Transactional
@EnableJpaAuditing
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityManager testEntityManager;

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
        testEntityManager.flush();

        System.out.println(savedUser.getId());
        System.out.println(savedUser.getDate());
        assertNotNull(savedUser);
        assertTrue(user.getId().equals(savedUser.getId()));
        assertTrue(user.getPassword().equals(savedUser.getPassword()));
    }

    @Test
    void save_duplicate_user_email() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("1111");
        user.setName("Name");
        user.setNickname("NickName");
        user.setAge(20);
        user.setGender("M");

        userRepository.save(user);
        testEntityManager.flush();

        User newuser = new User();
        newuser.setEmail("test@test.com");
        newuser.setPassword("1111");
        newuser.setName("Name");
        newuser.setNickname("NickName");
        newuser.setAge(20);
        newuser.setGender("M");

        userRepository.save(newuser);
        assertThrows(ConstraintViolationException.class, () -> {
            testEntityManager.flush();
        });
    }
}