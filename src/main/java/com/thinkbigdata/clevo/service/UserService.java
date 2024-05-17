package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.TokenDto;
import com.thinkbigdata.clevo.dto.UserDto;
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

    @PostConstruct
    private void init() {
        this.bytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

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
}
