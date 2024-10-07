package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import com.thinkbigdata.clevo.util.pronounce.PronounceApi;
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

    public LearningLogDto getRandomSentenceResult(String email, SentenceDto sentence) {
        User user = basicEntityService.getUserByEmail(email);

        double score = pronounceApi.getSentenceScore(sentence.getEng(), sentence.getBase64());
        double acr = (((int) (Math.random() * 11) - 5) / 10.0);
        double fcr = (((int) (Math.random() * 11) - 5) / 10.0);
        double accuracy = score + acr > 5.0 ? 5.0 : (score + acr < 1.0 ? 1.0 : score + acr);
        double fluency = score + fcr > 5.0 ? 5.0 : (score + fcr < 1.0 ? 1.0 : score + fcr);
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
        LearningLog log = LearningLog.builder().user(user).sentence(st).accuracy(accuracy).fluency(fluency).totalScore(totalScore).build();
        learningLogRepository.save(log);

        return basicEntityService.getLearningLogDto(log, basicEntityService.getSentenceDto(st));
    }

    public LearningLogDto getUserSentenceResult(String email, UserSentenceDto userSentenceDto) {
        User user = basicEntityService.getUserByEmail(email);
        Sentence sentence = basicEntityService.getSentence(userSentenceDto.getSentence_id());
        UserSentence userSentence = basicEntityService.getUserSentence(user, sentence);

        double score = pronounceApi.getSentenceScore(sentence.getEng(), userSentenceDto.getBase64());
        double acr = (((int) (Math.random() * 11) - 5) / 10.0);
        double fcr = (((int) (Math.random() * 11) - 5) / 10.0);
        double accuracy = score + acr > 5.0 ? 5.0 : (score + acr < 1.0 ? 1.0 : score + acr);
        double fluency = score + fcr > 5.0 ? 5.0 : (score + fcr < 1.0 ? 1.0 : score + fcr);
        double totalScore = (accuracy + fluency) / 2.0;

        LearningLog log = LearningLog.builder().user(user).sentence(sentence).accuracy(accuracy).fluency(fluency).totalScore(totalScore).build();
        learningLogRepository.save(log);

        return basicEntityService.getLearningLogDto(log, basicEntityService.getSentenceDto(sentence));
    }
}