package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class SentenceService {
    private final BasicEntityService basicEntityService;
    private final SentenceRepository sentenceRepository;
    private final LearningLogRepository learningLogRepository;
    private final UserSentenceRepository userSentenceRepository;

    public SentenceDto getSentenceById(Integer sentenceId) {
        return basicEntityService.getSentenceDto(basicEntityService.getSentence(sentenceId));
    }

    public List<UserSentenceDto> getUserSentences(String email) {
        User user = basicEntityService.getUserByEmail(email);

        List<UserSentenceDto> userSentences = new ArrayList<>();
        List<UserSentence> userSentenceList = userSentenceRepository.findByUser(user);

        for (UserSentence userSentence : userSentenceList) {
            SentenceDto sentenceDto = basicEntityService.getSentenceDto(userSentence.getSentence());
            List<LearningLog> logList = learningLogRepository.findBySentence(userSentence.getSentence());
            List<LearningLogDto> logDtoList = new ArrayList<>();
            for (LearningLog learningLog : logList) {
                LearningLogDto learningLogDto = basicEntityService.getLearningLogDto(learningLog, sentenceDto);
                logDtoList.add(learningLogDto);
            }
            userSentences.add(basicEntityService.getUserSentenceDto(userSentence, sentenceDto, logDtoList));
        }
        return userSentences;
    }

    public List<LearningLogDto> getUserLogs(String email) {
        User user = basicEntityService.getUserByEmail(email);

        List<LearningLog> learningLogs = learningLogRepository.findByUser(user);
        List<LearningLogDto> userLogs = new ArrayList<>();
        for (LearningLog learningLog : learningLogs) {
            LearningLogDto lld = basicEntityService.getLearningLogDto(learningLog, basicEntityService.getSentenceDto(learningLog.getSentence()));
            userLogs.add(lld);
        }
        return userLogs;
    }

    public void deleteUserSentenceById(Integer sentenceId, String email) {
        User user = basicEntityService.getUserByEmail(email);
        Sentence sentence = basicEntityService.getSentence(sentenceId);

        if (userSentenceRepository.findByUserAndSentence(user, sentence).isPresent()) {
            UserSentence userSentence = userSentenceRepository.findByUserAndSentence(user, sentence).get();
            userSentenceRepository.delete(userSentence);
        } else {
            throw new EntityNotFoundException("해당하는 사용자의 문장이 없습니다.");
        }
    }

    public void addUserSentence(String email, Integer sentenceId) {
        User user = basicEntityService.getUserByEmail(email);
        Sentence sentence = basicEntityService.getSentence(sentenceId);

        if (userSentenceRepository.findByUserAndSentence(user, sentence).isEmpty()) {
            UserSentence userSentence = new UserSentence();
            userSentence.setUser(user);
            userSentence.setSentence(sentence);
            userSentenceRepository.save(userSentence);
        } else {
            throw new EntityExistsException("해당하는 사용자의 문장이 이미 있습니다.");
        }
    }
}
