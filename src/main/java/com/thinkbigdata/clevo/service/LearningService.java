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
    private final UserRepository userRepository;
    private final SentenceRepository sentenceRepository;
    private final TopicRepository topicRepository;
    private final LearningLogRepository learningLogRepository;
    private final UserSentenceRepository userSentenceRepository;
    private final SentenceTopicRepository sentenceTopicRepository;
    private final UserTopicRepository userTopicRepository;
    private final PronounceApi pronounceApi;

    public LearningLogDto getRandomSentenceResult(String email, SentenceDto sentence) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        // api 요청
//        ObjectNode requests = JsonNodeFactory.instance.objectNode();
//        ObjectNode arguments = JsonNodeFactory.instance.objectNode();
//
//        arguments.put("language_code", "english");
//        arguments.put("script", sentence.getEng());
//        arguments.put("audio", sentence.getBase64());
//        requests.put("argument", arguments);

//        String result = null;
//        try {
//            result = pronounceApi.requestToServer(requests);
//        } catch (RestClientException e) {
//            throw new RuntimeException(e);
//        }

//        Map<String, Object> res = null;
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            res = objectMapper.readValue(result, Map.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

//        if ((Integer) res.get("result") == -1 )
//            throw new RuntimeException("API 호출 결과가 유효하지 않습니다.");
//
//        Map<String, String> value = (Map<String, String>) res.get("return_object");

        double score = pronounceApi.getSentenceScore(sentence.getEng(), sentence.getBase64());
        double acr = (((int) (Math.random() * 11) - 5) / 10.0);
        double fcr = (((int) (Math.random() * 11) - 5) / 10.0);
        double accuracy = score + acr > 5.0 ? 5.0 : (score + acr < 1.0 ? 1.0 : score + acr);
        double fluency = score + fcr > 5.0 ? 5.0 : (score + fcr < 1.0 ? 1.0 : score + fcr);
        double totalScore = accuracy + fluency / 2.0;

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

        return LearningLogDto.builder().id(log.getId()).email(log.getUser().getEmail()).sentence_id(log.getSentence().getId())
                .eng(log.getSentence().getEng()).kor(log.getSentence().getKor()).level(log.getSentence().getLevel())
                .accuracy(log.getAccuracy()).fluency(log.getFluency()).total_score(log.getTotalScore()).date(log.getDate()).build();
    }

    public LearningLogDto getUserSentenceResult(String email, UserSentenceDto userSentenceDto) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        Sentence sentence = sentenceRepository.findById(userSentenceDto.getSentence_id()).orElseThrow(() ->
                new NoSuchElementException("문장 정보가 없습니다."));

        UserSentence userSentence = userSentenceRepository.findById(userSentenceDto.getId()).orElseThrow(() ->
                new NoSuchElementException("문장 정보가 없습니다."));

        double score = pronounceApi.getSentenceScore(sentence.getEng(), userSentenceDto.getBase64());
        double acr = (((int) (Math.random() * 11) - 5) / 10.0);
        double fcr = (((int) (Math.random() * 11) - 5) / 10.0);
        double accuracy = score + acr > 5.0 ? 5.0 : (score + acr < 1.0 ? 1.0 : score + acr);
        double fluency = score + fcr > 5.0 ? 5.0 : (score + fcr < 1.0 ? 1.0 : score + fcr);
        double totalScore = accuracy + fluency / 2.0;

        userSentence.setFluency(score);
        userSentence.setAccuracy(score);
        userSentence.setTotalScore(score);

        LearningLog log = LearningLog.builder().user(user).sentence(sentence).accuracy(accuracy).fluency(fluency).totalScore(totalScore).build();
        learningLogRepository.save(log);

        return LearningLogDto.builder().id(log.getId()).email(log.getUser().getEmail()).sentence_id(log.getSentence().getId())
                .eng(log.getSentence().getEng()).kor(log.getSentence().getKor()).level(log.getSentence().getLevel())
                .accuracy(log.getAccuracy()).fluency(log.getFluency()).total_score(log.getTotalScore()).date(log.getDate()).build();
    }

}