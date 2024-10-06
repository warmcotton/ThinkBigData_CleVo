package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.category.Category;
import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
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
public class SentenceService {
    private final UserRepository userRepository;
    private final SentenceRepository sentenceRepository;
    private final LearningLogRepository learningLogRepository;
    private final UserSentenceRepository userSentenceRepository;
    private final SentenceTopicRepository sentenceTopicRepository;

    public Sentence getSentenceById(Integer sentenceId) {
        return sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new NoSuchElementException("문장 정보가 없습니다."));
    }

    public SentenceDto getSentenceDtoById(Integer sentenceId) {
        Sentence sentence =  sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new NoSuchElementException("문장 정보가 없습니다."));

        SentenceDto sentenceDto = new SentenceDto();
        sentenceDto.setId(sentence.getId());
        sentenceDto.setEng(sentence.getEng());
        sentenceDto.setKor(sentence.getKor());
        sentenceDto.setLevel(sentence.getLevel());

        List<Category> categories = new ArrayList<>();
        List<SentenceTopic> sts = sentenceTopicRepository.findBySentence(sentence);
        for (SentenceTopic s : sts) {
            categories.add(s.getTopic().getCategory());
        }
        sentenceDto.setCategories(categories);

        return sentenceDto;
    }

    public List<UserSentenceDto> getUserSentences(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        List<UserSentenceDto> userSentences = new ArrayList<>();
        List<UserSentence> userSentenceList = userSentenceRepository.findByUser(user);

        for (UserSentence userSentence : userSentenceList) {

            SentenceDto sentenceDto = new SentenceDto();
            sentenceDto.setId(userSentence.getSentence().getId());
            sentenceDto.setEng(userSentence.getSentence().getEng());
            sentenceDto.setKor(userSentence.getSentence().getKor());
            sentenceDto.setLevel(userSentence.getSentence().getLevel());

            List<Category> categories = new ArrayList<>();
            List<SentenceTopic> sts = sentenceTopicRepository.findBySentence(userSentence.getSentence());
            for (SentenceTopic s : sts) {
                categories.add(s.getTopic().getCategory());
            }
            sentenceDto.setCategories(categories);

            UserSentenceDto usd = UserSentenceDto.builder().id(userSentence.getId()).sentence_id(userSentence.getSentence().getId())
                    .sentence(sentenceDto).accuracy(userSentence.getAccuracy())
                    .fluency(userSentence.getFluency()).total_score(userSentence.getTotalScore())
                    .created_date(userSentence.getCreatedDate()).modified_date(userSentence.getModifiedDate()).build();

            userSentences.add(usd);
        }

        return userSentences;
    }

    public List<LearningLogDto> getUserLogs(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        List<LearningLogDto> userLogs = new ArrayList<>();
        List<LearningLog> learningLogs = learningLogRepository.findByUser(user);

        for (LearningLog learningLog : learningLogs) {
            LearningLogDto lld = LearningLogDto.builder().id(learningLog.getId()).email(learningLog.getUser().getEmail())
                    .sentence_id(learningLog.getSentence().getId()).eng(learningLog.getSentence().getEng())
                    .kor(learningLog.getSentence().getKor()).level(learningLog.getSentence().getLevel())
                    .accuracy(learningLog.getAccuracy()).fluency(learningLog.getFluency())
                    .total_score(learningLog.getTotalScore()).date(learningLog.getDate()).build();

            userLogs.add(lld);
        }

        return userLogs;
    }

    public void deleteUserSentenceById(Integer sentenceId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        Sentence sentence = sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new NoSuchElementException("문장 정보가 없습니다."));

        UserSentence userSentence = null;

        if (userSentenceRepository.findBySentence(sentence).isPresent()) {
            userSentence = userSentenceRepository.findBySentence(sentence).get();
            userSentenceRepository.delete(userSentence);
        }
    }

    public void addUserSentence(String email, Integer sentenceId, Double accuracy, Double fluency, Double totalScore) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        Sentence sentence = sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new NoSuchElementException("문장 정보가 없습니다."));

        UserSentence userSentence = null;

        if (userSentenceRepository.findBySentence(sentence).isEmpty()) {
            userSentence = new UserSentence();
            userSentence.setUser(user);
            userSentence.setSentence(sentence);
            userSentence.setAccuracy(accuracy);
            userSentence.setFluency(fluency);
            userSentence.setTotalScore(totalScore);

            userSentenceRepository.save(userSentence);
        }
    }
}
