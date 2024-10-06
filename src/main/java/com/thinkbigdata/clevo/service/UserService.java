package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.*;
import com.thinkbigdata.clevo.dto.user.*;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import com.thinkbigdata.clevo.category.Category;
import com.thinkbigdata.clevo.util.email.EmailSender;
import com.thinkbigdata.clevo.util.token.TokenGenerateValidator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
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
    private final SentenceService sentenceService;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerateValidator tokenGenerateValidator;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailSender emailSender;
    @Value("${img.location}") private String imgLocation;
    @Value("${jwt.secret}") private String secret;
    @Value("${token.access}") private Long accessExpired;
    @Value("${token.refresh}") private Long refreshExpired;
    private byte[] bytes;
    private Key key;
    private static final String PROFILE_DEFAULT_IMAGE = "default-profile.jpg";
    private static final Integer DEFAULT_TARGET = 5;

    @PostConstruct
    private void init() {
        this.bytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public UserDto registerUser(UserRegistrationDto registerDto, String sessionId) {
        if (!registerDto.getPassword2().equals(registerDto.getPassword1()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");

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
        if (redisTemplate.opsForValue().get("sessionId:"+sessionId) == null) {
            throw new RuntimeException("세션 정보 불일치");
        }
        String email = redisTemplate.opsForValue().get("sessionId:"+sessionId);
        User user = userRepository.findByEmail(email).get();

        user.setLevel(userInfoDto.getLevel());
        user.setTarget(DEFAULT_TARGET);

        List<UserTopic> topics = userTopicRepository.findByUser(user);
        if (topics.size() != 0)
            userTopicRepository.deleteAll(topics);

        List<UserTopic> userTopics = new ArrayList<>();
        for (Category category : userInfoDto.getCategory()) {
            Topic T = topicRepository.findByCategory(category).get();
            UserTopic userTopic = new UserTopic();
            userTopic.setTopic(T);
            userTopic.setUser(user);
            userTopics.add(userTopic);
        }

        if (userTopics.size()!=0) {
            userTopics = userTopicRepository.saveAll(userTopics);
        }

        List<Category> categories = new ArrayList<>();
        for (UserTopic userTopic: userTopics) {
            categories.add(userTopic.getTopic().getCategory());
        }
        UserImage userImage = userImageRepository.findByUser(user).get();

        return getUserDto(user, categories, userImage);
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
        return UserDto.builder().email(user.getEmail()).name(user.getName()).nickname(user.getNickname()).birth(user.getBirth())
                .gender(user.getGender()).level(user.getLevel()).target(user.getTarget()).role(user.getRole()).img_path(image.getPath())
                .created_date(user.getDate()).lastLogin_date(user.getLast()).build();
    }

    private UserDto getUserDto(User user, List<Category> topicList, UserImage image) {
        return UserDto.builder().email(user.getEmail()).name(user.getName()).nickname(user.getNickname()).birth(user.getBirth())
                .gender(user.getGender()).level(user.getLevel()).target(user.getTarget()).role(user.getRole()).img_path(image.getPath())
                .category(topicList).created_date(user.getDate()).lastLogin_date(user.getLast()).build();
    }

    //Generate Access, Refresh Token
    public TokenDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("비밀번호를 확인해주세요.");

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

        User user = userRepository.findByEmail(refreshToken.getEmail()).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));
        TokenDto token = tokenGenerateValidator.generateToken(user);
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.getRefresh()).getBody().getExpiration();

        refreshToken.setExpiredDate(new Timestamp(expiration.getTime()).toLocalDateTime());
        refreshToken.setValue(token.getRefresh());
        return token;
    }

    public UserDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));
        List<UserTopic> topic = userTopicRepository.findByUser(user);
        List<Category> categories = new ArrayList<>();
        for (UserTopic userTopic: topic) {
            categories.add(userTopic.getTopic().getCategory());
        }
        UserImage userImage = userImageRepository.findByUser(user).get();

        return getUserDto(user, categories, userImage);
    }

    // level category 수정
    public UserDto updateUserInfo(String email, UserInfoUpdateDto updateDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));
        UserImage savedImage = userImageRepository.findByUser(user).get();
        List<UserTopic> topics = userTopicRepository.findByUser(user);
        List<Category> categories = new ArrayList<>();

        if (updateDto.getNickname() != null) user.setNickname(updateDto.getNickname());
        if (updateDto.getLevel() != null) user.setLevel(updateDto.getLevel());
//        if (updateDto.getTarget() != null) user.setTarget(updateDto.getTarget());
        if (updateDto.getCategory() != null) {
            userTopicRepository.deleteAll(topics);

            List<UserTopic> userTopics = new ArrayList<>();
            for (Category topic : updateDto.getCategory()) {
                Topic T = topicRepository.findByCategory(topic).get();
                UserTopic userTopic = new UserTopic();
                userTopic.setTopic(T);
                userTopic.setUser(user);
                userTopics.add(userTopic);
            }
            if (userTopics.size()!=0) {
                userTopics = userTopicRepository.saveAll(userTopics);
            }

            for (UserTopic userTopic: userTopics) {
                categories.add(userTopic.getTopic().getCategory());
            }
        } else {
            for (UserTopic userTopic: topics) {
                categories.add(userTopic.getTopic().getCategory());
            }
        }
//        if (userImage != null ) {
//            if (!savedImage.getName().equals(PROFILE_DEFAULT_IMAGE))
//                deleteImage(savedImage.getName());
//            UserImage newImage = saveImage(userImage);
//            savedImage.setName(newImage.getName());
//            savedImage.setOriginName(newImage.getOriginName());
//            savedImage.setPath(newImage.getPath());
//        }

        return getUserDto(user, categories, savedImage);
    }

    // nickname, image, password 수정
    public UserDto updateUserProfile(String email, UserProfileUpdateDto updateDto, MultipartFile userImage) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));
        UserImage savedImage = userImageRepository.findByUser(user).get();
        List<UserTopic> topics = userTopicRepository.findByUser(user);
        List<Category> categories = new ArrayList<>();

        if (updateDto.getNickname() != null) user.setNickname(updateDto.getNickname());
        if (updateDto.getEmail() != null) user.setEmail(updateDto.getEmail());
        if (userImage != null ) {
            if (!savedImage.getName().equals(PROFILE_DEFAULT_IMAGE))
                deleteImage(savedImage.getName());
            UserImage newImage = saveImage(userImage);
            savedImage.setName(newImage.getName());
            savedImage.setOriginName(newImage.getOriginName());
            savedImage.setPath(newImage.getPath());
        }
        if (updateDto.getEx_password() != null && updateDto.getNew_password1() != null && updateDto.getNew_password2() != null) {
            if (!updateDto.getNew_password1().equals(updateDto.getNew_password2()))
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            if (!passwordEncoder.matches(updateDto.getEx_password(), user.getPassword()))
                throw new RuntimeException("비밀번호를 확인해주세요.");
            user.setPassword(passwordEncoder.encode(updateDto.getNew_password1()));
        }
        for (UserTopic userTopic: topics) {
            categories.add(userTopic.getTopic().getCategory());
        }

        return getUserDto(user, categories, savedImage);
    }

    private void deleteImage(String name) {
        File file = new File(imgLocation+"/"+name);
        if (file.exists()) {
            file.delete();
        }
    }

    public void findPassword(String email, String name, String birth) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        if (!user.getName().equals(name)) throw new BadCredentialsException("가입된 이름과 일치하지 않습니다.");
        if (!user.getBirth().toString().equals(birth)) throw new BadCredentialsException("가입된 생년월일과 일치하지 않습니다.");

        String password = generatePassword();
        user.setPassword(passwordEncoder.encode(password));

        try {
            emailSender.sendMail(email, name, password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String num = "012345789";
        String sp = "!@#$%&*?";

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();

        sb.append(upper.charAt(sr.nextInt(upper.length())));
        for(int i=0; i<3; i++) {
            sb.append(lower.charAt(sr.nextInt(lower.length())));
        }
        for(int i=0; i<4; i++) {
            sb.append(num.charAt(sr.nextInt(num.length())));
        }
        sb.append(sp.charAt(sr.nextInt(sp.length())));
        return sb.toString();
    }

    public void updatePassword(String email, PasswordUpdateDto passwordDto) {
        if (!passwordDto.getNewPassword1().equals(passwordDto.getNewPassword2()))
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        if (!passwordEncoder.matches(passwordDto.getExPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호를 확인해주세요.");

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword1()));
    }

    public UserDashBoardDto userDashboard(String email) {
        UserDashBoardDto dashBoardDto = new UserDashBoardDto();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));
        List<UserTopic> topic = userTopicRepository.findByUser(user);
        List<Category> categories = new ArrayList<>();
        for (UserTopic userTopic: topic) {
            categories.add(userTopic.getTopic().getCategory());
        }
        UserImage userImage = userImageRepository.findByUser(user).get();

        dashBoardDto.setUser(getUserDto(user, categories, userImage));
        dashBoardDto.setUser_sentences(sentenceService.getUserSentences(email));
        dashBoardDto.setLearning_logs(sentenceService.getUserLogs(email));

        return dashBoardDto;
    }
}
