package com.thinkbigdata.clevo.config;

import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.enums.Role;
import com.thinkbigdata.clevo.repository.TopicRepository;
import com.thinkbigdata.clevo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class BootstrapCommanderLinerRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        User admin = User.builder().email("admin@admin.com").name("admin").nickname("admin")
                .birth(LocalDate.now()).gender("M")
                .build();
        admin.setPassword(passwordEncoder.encode("Admin1111!"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }
}
