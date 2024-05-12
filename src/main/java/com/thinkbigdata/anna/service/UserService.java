package com.thinkbigdata.anna.service;

import com.thinkbigdata.anna.dto.UserDto;
import com.thinkbigdata.anna.dto.UserRegistrationDto;
import com.thinkbigdata.anna.entity.Topic;
import com.thinkbigdata.anna.entity.User;
import com.thinkbigdata.anna.entity.UserImage;
import com.thinkbigdata.anna.entity.UserTopic;
import com.thinkbigdata.anna.repository.TopicRepository;
import com.thinkbigdata.anna.repository.UserImageRepository;
import com.thinkbigdata.anna.repository.UserRepository;
import com.thinkbigdata.anna.repository.UserTopicRepository;
import com.thinkbigdata.anna.topic.TopicName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserTopicRepository userTopicRepository;
    private final TopicRepository topicRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${img.location}") private String imgLocation;

    public UserDto registerUser(UserRegistrationDto registerDto, MultipartFile imageFile) {
        User user = User.builder().email(registerDto.getEmail()).name(registerDto.getName()).nickname(registerDto.getNickName())
                .age(registerDto.getAge()).gender(registerDto.getGender()).level(registerDto.getLevel()).target(registerDto.getTarget())
                .build();
        user.setPassword(passwordEncoder.encode(registerDto.getPassword1()));
        User savedUser = userRepository.save(user);

        List<UserTopic> userTopics = new ArrayList<>();
        for (TopicName topic : registerDto.getTopic()) {
            Topic T = topicRepository.findByTopicName(topic).get();
            UserTopic userTopic = new UserTopic();
            userTopic.setTopic(T);
            userTopic.setUser(savedUser);
            userTopics.add(userTopic);
        }

        if (userTopics.size()!=0) {
            userTopics = userTopicRepository.saveAll(userTopics);
        }
        
        List<TopicName> topicNames = new ArrayList<>();
        for (UserTopic userTopic: userTopics) {
            topicNames.add(userTopic.getTopic().getTopicName());
        }

        UserImage userImage = saveImage(imageFile);
        userImage.setUser(savedUser);
        UserImage savedUserImage = userImageRepository.save(userImage);

        return UserDto.builder().email(savedUser.getEmail()).name(savedUser.getName()).nickName(savedUser.getNickname()).age(savedUser.getAge())
                .gender(savedUser.getGender()).level(savedUser.getLevel()).target(savedUser.getTarget()).role(savedUser.getRole())
                .imgPath(savedUserImage.getPath()).createdDate(savedUser.getDate()).lastLoginDate(savedUser.getLast()).topic(topicNames).build();
    }

    private UserImage saveImage(MultipartFile imageFile) {
        String originName = imageFile.getOriginalFilename();
        String extension = originName.substring(originName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String path = "/images/anna/"+savedFileName;

        try {
            FileOutputStream fos = new FileOutputStream(imgLocation+"/"+savedFileName);
            fos.write(imageFile.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserImage userImage = new UserImage();
        userImage.setName(savedFileName);
        userImage.setOriginName(originName);
        userImage.setPath(path);
        return userImage;
    }
}
