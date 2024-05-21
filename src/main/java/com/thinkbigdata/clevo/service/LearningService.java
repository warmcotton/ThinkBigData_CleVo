package com.thinkbigdata.clevo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thinkbigdata.clevo.dto.LearningLogDto;
import com.thinkbigdata.clevo.dto.SentenceDto;
import com.thinkbigdata.clevo.entity.*;
import com.thinkbigdata.clevo.repository.*;
import com.thinkbigdata.clevo.util.pronounce.PronounceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningService {
    private final UserRepository userRepository;
    private final SentenceRepository sentenceRepository;
    private final UserTopicRepository userTopicRepository;
    private final LearningLogRepository learningLogRepository;
    private final UserRecordRepository userRecordRepository;
    private final PronounceApi pronounceApi;
    @Value("${record.location}") private String recordLocation;
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

    public LearningLogDto getScore(String email, Integer sentenceId, MultipartFile record) {
        User user = userRepository.findByEmail(email).get();
        Sentence sentence = sentenceRepository.findById(sentenceId).orElseThrow(() ->
                new RuntimeException("문장 정보가 없습니다."));

        String languageCode = "english";
        String script = sentence.getEng();
        UserRecord userRecord = saveRecord(record);
        String audioContents = null;

        ObjectNode requests = JsonNodeFactory.instance.objectNode();
        ObjectNode arguments = JsonNodeFactory.instance.objectNode();

        try {
            Path path = Paths.get(recordLocation+"/"+userRecord.getName());
            byte[] audioBytes = Files.readAllBytes(path);
            audioContents = Base64.getEncoder().encodeToString(audioBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        arguments.put("language_code", languageCode);
        arguments.put("script", script);
        arguments.put("audio", audioContents);
        requests.put("argument", arguments);

        //api
        Map<String, String> result = null;
        try {
             result = pronounceApi.requestToServer(requests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (result.get("result").equals("-1"))
            throw new RuntimeException("api 요청 오류");

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> sc = null;
        try {
            sc = mapper.readValue(result.get("return_object"), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("파싱 오류");
        }

        userRecordRepository.save(userRecord);
        LearningLog log = LearningLog.builder().user(user).sentence(sentence).record(userRecord)
                        .clarity(Integer.valueOf(sc.get("score"))).fluency(Integer.valueOf(sc.get("score"))).totalScore(5).build();
        LearningLog learningLog = learningLogRepository.saveAndFlush(log);

        return LearningLogDto.builder().id(learningLog.getId()).user(user.getEmail()).sentence(sentence.getId())
                .path(userRecord.getPath()).clarity(learningLog.getClarity()).fluency(learningLog.getFluency())
                .average(learningLog.getTotalScore()).time(learningLog.getDate()).build();
    }

    private UserRecord saveRecord(MultipartFile record) {
        String originName = record.getOriginalFilename();
        String extension = originName.substring(originName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String path = "/records/clevo/"+savedFileName;

        try {
            FileOutputStream fos = new FileOutputStream(recordLocation+"/"+savedFileName);
            fos.write(record.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserRecord userRecord = new UserRecord();
        userRecord.setName(savedFileName);
        userRecord.setOriginName(originName);
        userRecord.setPath(path);

        return userRecord;
    }
}
