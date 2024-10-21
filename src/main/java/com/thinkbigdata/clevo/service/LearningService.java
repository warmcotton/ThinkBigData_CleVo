package com.thinkbigdata.clevo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.exception.InsufficientUserInfoException;
import com.thinkbigdata.clevo.exception.PronounceEvaluationException;
import com.thinkbigdata.clevo.repository.*;
import com.thinkbigdata.clevo.util.pronounce.PronounceApi;
import com.thinkbigdata.clevo.util.pronounce.PronunciationEvaluator;
import static com.thinkbigdata.clevo.util.pronounce.PronunciationEvaluator.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningService {
    private final BasicEntityService basicEntityService;
    private final SentenceRepository sentenceRepository;
    private final LearningLogRepository learningLogRepository;
    private final SentenceTopicRepository sentenceTopicRepository;
    private final UserTopicRepository userTopicRepository;
    private final PronounceApi pronounceApi;
    private final PronunciationEvaluator pronunciationEvaluator;

    public LearningLogDto getRandomSentenceResult(String email, SentenceDto sentence) throws JsonProcessingException, InsufficientUserInfoException, PronounceEvaluationException {
        User user = basicEntityService.getUserByEmail(email);
        // user info 예외처리
        if (userTopicRepository.findByUser(user).size() == 0 || user.getLevel() == null) throw new InsufficientUserInfoException("학습을 위해 필요한 사용자 정보가 부족합니다");

        String recognized = pronounceApi.getSentenceScript(sentence.getBase64());
        Result result = pronunciationEvaluator.evaluatePronunciation(recognized, sentence.getEng());
        double fluency = pronounceApi.getSentenceScore(sentence.getEng(), sentence.getBase64());
        double accuracy = result.getScore2();
        String vulnerable = result.getVulnerable();
        double totalScore = (accuracy + fluency) / 2.0;

        Optional<Sentence> optst = sentenceRepository.findByEng(sentence.getEng());
        Sentence st = null;

        if (optst.isEmpty()) {
            st = new Sentence();
            st.setEng(sentence.getEng());
            st.setKor(sentence.getKor());
            st.setLevel(user.getLevel());
            sentenceRepository.save(st);

            List<SentenceTopic> sentenceTopics = new ArrayList<>();
            List<UserTopic> topics = userTopicRepository.findByUser(user);

            for (UserTopic topic : topics) {
                SentenceTopic sentenceTopic = new SentenceTopic();
                sentenceTopic.setTopic(topic.getTopic());
                sentenceTopic.setSentence(st);
                sentenceTopics.add(sentenceTopic);
            }
            sentenceTopicRepository.saveAll(sentenceTopics);
        } else {
            st = optst.get();
        }
        LearningLog log = LearningLog.builder().user(user).sentence(st).accuracy(accuracy).fluency(fluency).vulnerable(vulnerable).totalScore(totalScore).build();
        learningLogRepository.save(log);

        return basicEntityService.getLearningLogDto(log, basicEntityService.getSentenceDto(st));
    }

    public LearningLogDto getUserSentenceResult(String email, UserSentenceDto userSentenceDto) throws JsonProcessingException, InsufficientUserInfoException, PronounceEvaluationException {
        User user = basicEntityService.getUserByEmail(email);
        // user info 예외처리
        if (userTopicRepository.findByUser(user).size() == 0 || user.getLevel() == null) throw new InsufficientUserInfoException("학습을 위해 필요한 사용자 정보가 부족합니다");

        Sentence sentence = basicEntityService.getSentence(userSentenceDto.getSentence_id());
        UserSentence userSentence = basicEntityService.getUserSentence(user, sentence);

        String recognized = pronounceApi.getSentenceScript(userSentenceDto.getBase64());
        Result result = pronunciationEvaluator.evaluatePronunciation(recognized, sentence.getEng());
        double fluency = pronounceApi.getSentenceScore(sentence.getEng(), userSentenceDto.getBase64());
        double accuracy = result.getScore2();
        String vulnerable = result.getVulnerable();
        double totalScore = (accuracy + fluency) / 2.0;

        LearningLog log = LearningLog.builder().user(user).sentence(sentence).accuracy(accuracy).fluency(fluency).vulnerable(vulnerable).totalScore(totalScore).build();
        learningLogRepository.save(log);

        return basicEntityService.getLearningLogDto(log, basicEntityService.getSentenceDto(sentence));
    }
}