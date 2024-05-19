package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.SentenceDto;
import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.entity.UserTopic;
import com.thinkbigdata.clevo.repository.SentenceRepository;
import com.thinkbigdata.clevo.repository.TopicRepository;
import com.thinkbigdata.clevo.repository.UserRepository;
import com.thinkbigdata.clevo.repository.UserTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningService {
    private final UserRepository userRepository;
    private final SentenceRepository sentenceRepository;
    private final UserTopicRepository userTopicRepository;
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
}
