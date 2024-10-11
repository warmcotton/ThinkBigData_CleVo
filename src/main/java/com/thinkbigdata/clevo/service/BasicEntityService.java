package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.enums.Category;
import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.dto.user.UserDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicEntityService {
    private final UserRepository userRepository;
    private final UserTopicRepository userTopicRepository;
    private final UserImageRepository userImageRepository;
    private final SentenceRepository sentenceRepository;
    private final SentenceTopicRepository sentenceTopicRepository;
    private final LearningLogRepository learningLogRepository;
    private final UserSentenceRepository userSentenceRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));
    }

    public UserDto getUserDto(User user) {
        List<UserTopic> topic = userTopicRepository.findByUser(user);
        List<Category> categories = new ArrayList<>();
        for (UserTopic userTopic: topic) {
            categories.add(userTopic.getTopic().getCategory());
        }
        UserImage userImage = userImageRepository.findByUser(user).get();
        return UserDto.builder().email(user.getEmail()).name(user.getName()).nickname(user.getNickname()).birth(user.getBirth())
                .gender(user.getGender()).level(user.getLevel()).target(user.getTarget()).role(user.getRole()).img_path(userImage.getPath())
                .category(categories).created_date(user.getDate()).lastLogin_date(user.getLast()).build();
    }

    public Sentence getSentence(Integer sentenceId) {
        return sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new EntityNotFoundException("해당하는 문장 정보가 없습니다."));
    }

    public SentenceDto getSentenceDto(Sentence sentence) {
        List<Category> categories = new ArrayList<>();
        List<SentenceTopic> sts = sentenceTopicRepository.findBySentence(sentence);
        for (SentenceTopic s : sts) {
            categories.add(s.getTopic().getCategory());
        }
        return SentenceDto.builder().id(sentence.getId()).eng(sentence.getEng()).kor(sentence.getKor())
                .level(sentence.getLevel()).categories(categories).build();
    }

    public LearningLog getLearningLog(Integer logId) {
        return learningLogRepository.findById(logId).orElseThrow(() ->
                new EntityNotFoundException("해당하는 로그 정보가 없습니다."));
    }

    public LearningLogDto getLearningLogDto(LearningLog learningLog, SentenceDto sentenceDto) {
        return LearningLogDto.builder().id(learningLog.getId()).email(learningLog.getUser().getEmail())
                .sentence_id(learningLog.getSentence().getId()).sentenceDto(sentenceDto)
                .accuracy(learningLog.getAccuracy()).fluency(learningLog.getFluency())
                .total_score(learningLog.getTotalScore()).date(learningLog.getDate()).build();
    }

    public UserSentence getUserSentence(Integer id) {
        return userSentenceRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("해당하는 사용자의 문장 정보가 없습니다."));
    }

    public UserSentence getUserSentence(User user, Sentence sentence) {
        return userSentenceRepository.findByUserAndSentence(user, sentence).orElseThrow(() ->
                new EntityNotFoundException("해당하는 사용자의 문장 정보가 없습니다."));
    }

    public UserSentenceDto getUserSentenceDto(UserSentence userSentence, SentenceDto sentenceDto, List<LearningLogDto> logs) {
        return UserSentenceDto.builder().sentence_id(userSentence.getSentence().getId()).sentence(sentenceDto).
                logs(logs).build();
    }
}
