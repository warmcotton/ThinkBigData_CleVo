package com.thinkbigdata.clevo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thinkbigdata.clevo.dto.SentenceDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import com.thinkbigdata.clevo.topic.TopicName;
import com.thinkbigdata.clevo.util.pronounce.PronounceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningService {
    private final UserRepository userRepository;
    private final SentenceRepository sentenceRepository;
    private final UserTopicRepository userTopicRepository;
    private final TopicRepository topicRepository;
    private final LearningLogRepository learningLogRepository;
    private final PronounceApi pronounceApi;
    public List<SentenceDto> getNewSentences(String email) {
        User user = userRepository.findByEmail(email).get();

        List<UserTopic> topics = userTopicRepository.findByUser(user);

        if (user.getLevel() == null || user.getTarget() == null || topics.size() == 0 )
            throw new RuntimeException("정보를 마저 입력해주세요.");

        //api 대체
        List<Sentence> sentences = sentenceRepository.findAllByTopicInAndLevel(topics.stream().map(t -> t.getTopic()).collect(Collectors.toSet()), user.getLevel(), PageRequest.of(0, user.getTarget()));

        return sentences.stream().map(sentence -> SentenceDto.builder().id(sentence.getId()).topic(sentence.getTopic().getTopicName())
                .eng(sentence.getEng()).kor(sentence.getKor()).level(sentence.getLevel()).build()).collect(Collectors.toList());
    }

    public SentenceDto getSentence(Integer sentenceId) {
        Sentence sentence = sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new RuntimeException("문장 정보가 없습니다."));
        return SentenceDto.builder().id(sentence.getId()).topic(sentence.getTopic().getTopicName())
                .eng(sentence.getEng()).kor(sentence.getKor()).level(sentence.getLevel()).build();
    }

    public void saveSentence() {
        Sentence sentence = new Sentence();
        sentence.setEng("eng");
        sentence.setKor("한");
        sentence.setTopic(topicRepository.findByTopicName(TopicName.TOPIC1).get());
        sentence.setLevel(8);
        sentenceRepository.save(sentence);
    }

    public Float getResult(String email, SentenceDto sentence) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("가입된 이메일 정보가 없습니다."));

        Topic topic = topicRepository.findByTopicName(sentence.getTopic()).orElseThrow(() ->
                new NoSuchElementException("토픽 정보가 없습니다."));

        Optional<Sentence> optst = sentenceRepository.findByEng(sentence.getEng());
        Sentence st = null;

        //없다면 새로 저장
        if (optst.isEmpty()) {
            st = new Sentence();
            st.setEng(sentence.getEng());
            st.setKor(sentence.getKor());
            st.setTopic(topic);
            st.setLevel(sentence.getLevel());
            sentenceRepository.save(st);
        } else {
            st = optst.get();
        }

        // api 요청
        ObjectNode requests = JsonNodeFactory.instance.objectNode();
        ObjectNode arguments = JsonNodeFactory.instance.objectNode();

        arguments.put("language_code", "english");
        arguments.put("audio", sentence.getBase64());
        requests.put("argument", arguments);

        String result = null;
        try {
            result = pronounceApi.requestToServer(requests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> res = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.readValue(result, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if ((Integer) res.get("result") == -1 )
            throw new RuntimeException("API 호출 결과가 유효하지 않습니다.");

        Map<String, String> value = (Map<String, String>) res.get("return_object");

        float score = Float.parseFloat(value.get("score")); //parsing error 처리
        // 결과 저장
        LearningLog log = LearningLog.builder().user(user).sentence(st).clarity(score).fluency(score).totalScore(score).build();
        learningLogRepository.save(log);

        return score;
    }
}