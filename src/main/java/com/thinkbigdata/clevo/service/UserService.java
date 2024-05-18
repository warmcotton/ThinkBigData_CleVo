package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.TokenDto;
import com.thinkbigdata.clevo.dto.UserDto;
import com.thinkbigdata.clevo.dto.UserInfoDto;
import com.thinkbigdata.clevo.dto.UserRegistrationDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import com.thinkbigdata.clevo.topic.TopicName;
import com.thinkbigdata.clevo.util.token.TokenGenerateValidator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserTopicRepository userTopicRepository;
    private final TopicRepository topicRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerateValidator tokenGenerateValidator;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${img.location}") private String imgLocation;
    @Value("${jwt.secret}") private String secret;
    @Value("${token.access}") private Long accessExpired;
    @Value("${token.refresh}") private Long refreshExpired;
    private byte[] bytes;
    private Key key;
    private static final String PROFILE_DEFAULT_IMAGE = "default-profile.jpg";


    @PostConstruct
    private void init() {
        this.bytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public UserDto registerUser(UserRegistrationDto registerDto, String sessionId) {
        User user = User.builder().email(registerDto.getEmail()).name(registerDto.getName()).nickname(registerDto.getNickName())
                .birth(registerDto.getBirth()).gender(registerDto.getGender())
                .build();
        user.setPassword(passwordEncoder.encode(registerDto.getPassword1()));
        User savedUser = userRepository.save(user);

        UserImage userImage = saveDefaultImage();
        userImage.setUser(savedUser);
        UserImage savedUserImage = userImageRepository.save(userImage);

        redisTemplate.opsForValue().set("sessionId:"+sessionId, user.getEmail(), 300000L, TimeUnit.MILLISECONDS);
        return getUserDto(savedUser, savedUserImage);
    }

    public UserDto addUserInfo(UserInfoDto userInfoDto, String sessionId) {
        String email = "";
        if (redisTemplate.opsForValue().get("sessionId:"+sessionId) == null) {
            throw new RuntimeException("세션 정보 불일치");
        }
        email = redisTemplate.opsForValue().get("sessionId:"+sessionId);
        User user = userRepository.findByEmail(email).get();

        user.setLevel(userInfoDto.getLevel());
        user.setTarget(userInfoDto.getTarget());

        List<UserTopic> userTopics = new ArrayList<>();
        for (TopicName topic : userInfoDto.getTopic()) {
            Topic T = topicRepository.findByTopicName(topic).get();
            UserTopic userTopic = new UserTopic();
            userTopic.setTopic(T);
            userTopic.setUser(user);
            userTopics.add(userTopic);
        }

        if (userTopics.size()!=0) {
            userTopics = userTopicRepository.saveAll(userTopics);
        }

        List<TopicName> topicNames = new ArrayList<>();
        for (UserTopic userTopic: userTopics) {
            topicNames.add(userTopic.getTopic().getTopicName());
        }
        UserImage userImage = userImageRepository.findByUser(user).get();

        return getUserDto(user, topicNames, userImage);
    }

    private UserImage saveImage(MultipartFile imageFile) {
        String originName = imageFile.getOriginalFilename();
        String extension = originName.substring(originName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String path = "/images/clevo/"+savedFileName;

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

    private UserImage saveDefaultImage() {
        String originName = PROFILE_DEFAULT_IMAGE;
        String savedFileName = PROFILE_DEFAULT_IMAGE;
        String path = "/images/clevo/"+ PROFILE_DEFAULT_IMAGE;

        UserImage userImage = new UserImage();
        userImage.setName(savedFileName);
        userImage.setOriginName(originName);
        userImage.setPath(path);
        return userImage;
    }

    private UserDto getUserDto(User user, UserImage image) {
        return UserDto.builder().email(user.getEmail()).name(user.getName()).nickName(user.getNickname()).birth(user.getBirth())
                .gender(user.getGender()).level(user.getLevel()).target(user.getTarget()).role(user.getRole()).imgPath(image.getPath())
                .createdDate(user.getDate()).lastLoginDate(user.getLast()).build();
    }

    private UserDto getUserDto(User user, List<TopicName> topicList, UserImage image) {
        return UserDto.builder().email(user.getEmail()).name(user.getName()).nickName(user.getNickname()).birth(user.getBirth())
                .gender(user.getGender()).level(user.getLevel()).target(user.getTarget()).role(user.getRole()).imgPath(image.getPath())
                .topic(topicList).createdDate(user.getDate()).lastLoginDate(user.getLast()).build();
    }

    //Generate Access, Refresh Token
    public TokenDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("이메일 확인"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("패스워드 확인");

        TokenDto token = tokenGenerateValidator.generateToken(user);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(user.getEmail());
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.getRefresh()).getBody().getExpiration();

        if (refreshToken.isPresent()) {
            refreshToken.get().setValue(token.getRefresh());
            refreshToken.get().setExpiredDate(new Timestamp(expiration.getTime()).toLocalDateTime());
        } else {
            RefreshToken newToken = new RefreshToken();
            newToken.setEmail(user.getEmail());
            newToken.setValue(token.getRefresh());
            newToken.setExpiredDate(new Timestamp(expiration.getTime()).toLocalDateTime());
            refreshTokenRepository.save(newToken);
        }
        user.setLast(LocalDateTime.now());

        return token;
    }

    public void logout(String token, String email) {
        redisTemplate.opsForValue().set("logout:"+token,email,accessExpired,TimeUnit.MILLISECONDS);
    }

    public TokenDto refreshToken(String requestToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByValue(requestToken).orElseThrow(() ->
                new RuntimeException("토큰 정보 없음"));

        if (LocalDateTime.now().isAfter(refreshToken.getExpiredDate())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("토큰 정보 만료");
        }

        User user = userRepository.findByEmail(refreshToken.getEmail()).get();
        TokenDto token = tokenGenerateValidator.generateToken(user);
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.getRefresh()).getBody().getExpiration();

        refreshToken.setExpiredDate(new Timestamp(expiration.getTime()).toLocalDateTime());
        refreshToken.setValue(token.getRefresh());
        return token;
    }

    public UserDto getUser(String email) {
        User user = userRepository.findByEmail(email).get();
        List<UserTopic> topic = userTopicRepository.findByUser(user);
        List<TopicName> topicNames = new ArrayList<>();
        for (UserTopic userTopic: topic) {
            topicNames.add(userTopic.getTopic().getTopicName());
        }
        UserImage userImage = userImageRepository.findByUser(user).get();

        return getUserDto(user, topicNames, userImage);
    }

    public UserDto updateUser(String loginAccountEmail, UserDto updateDto, MultipartFile userImage) {
        if (!loginAccountEmail.equals(updateDto.getEmail())) throw new RuntimeException("유저 정보 불일치");

        User user = userRepository.findByEmail(updateDto.getEmail()).get();
        user.setNickname(updateDto.getNickName());
        user.setLevel(updateDto.getLevel());
        user.setTarget(updateDto.getTarget());

        List<UserTopic> topics = userTopicRepository.findByUser(user);
        userTopicRepository.deleteAll(topics);

        List<UserTopic> userTopics = new ArrayList<>();
        for (TopicName topic : updateDto.getTopic()) {
            Topic T = topicRepository.findByTopicName(topic).get();
            UserTopic userTopic = new UserTopic();
            userTopic.setTopic(T);
            userTopic.setUser(user);
            userTopics.add(userTopic);
        }
        if (userTopics.size()!=0) {
            userTopics = userTopicRepository.saveAll(userTopics);
        }

        List<TopicName> topicNames = new ArrayList<>();
        for (UserTopic userTopic: userTopics) {
            topicNames.add(userTopic.getTopic().getTopicName());
        }

        UserImage savedImage = userImageRepository.findByUser(user).get();
        UserImage newImage = saveImage(userImage);
        savedImage.setName(newImage.getName());
        savedImage.setOriginName(newImage.getOriginName());
        savedImage.setPath(newImage.getPath());

        return getUserDto(user, topicNames, savedImage);
    }
}
