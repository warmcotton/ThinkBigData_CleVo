package com.thinkbigdata.clevo.config;

import com.thinkbigdata.clevo.entity.Topic;
import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.repository.TopicRepository;
import com.thinkbigdata.clevo.repository.UserRepository;
import com.thinkbigdata.clevo.role.Role;
import com.thinkbigdata.clevo.topic.TopicName;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class BootstrapCommanderLinerRunner implements CommandLineRunner {
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        Topic topic1 = new Topic();
        Topic topic2 = new Topic();
        Topic topic3 = new Topic();
        User admin = User.builder().email("admin@admin.com").name("admin").nickname("admin")
                .age(20).gender("M").level(10).target(5)
                .build();
        admin.setPassword(passwordEncoder.encode("Admin1111!"));
        admin.setRole(Role.ADMIN);

        topic1.setTopicName(TopicName.TOPIC1);
        topic2.setTopicName(TopicName.TOPIC2);
        topic3.setTopicName(TopicName.TOPIC3);

        topicRepository.saveAll(Arrays.asList(topic1, topic2, topic3));
        userRepository.save(admin);
    }
}
