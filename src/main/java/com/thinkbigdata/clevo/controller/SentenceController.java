package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SentenceController {
    private final SentenceService sentenceService;

    @GetMapping("/sentence/{sentence_id}")
    public ResponseEntity<Sentence> getSentenceById(@PathVariable("sentence_id") Integer sentenceId) {
        if (sentenceId == null || sentenceId <= 0)
            throw new RuntimeException("bad request");

        Sentence sentence = sentenceService.getSentenceById(sentenceId);
        return ResponseEntity.ok(sentence);
    }

    @GetMapping("/sentence/user/sentences")
    public ResponseEntity<List<UserSentenceDto>> getUserSentences(Authentication authentication) {
        List<UserSentenceDto> userSentences = sentenceService.getUserSentences(authentication.getName());
        return ResponseEntity.ok(userSentences);
    }

    @GetMapping("/sentence/user/logs")
    public ResponseEntity<List<LearningLogDto>> getUserLogs(Authentication authentication) {
        List<LearningLogDto> learningLogs = sentenceService.getUserLogs(authentication.getName());
        return ResponseEntity.ok(learningLogs);
    }

    @DeleteMapping("/sentence/user/{sentence_id}")
    public ResponseEntity<?> deleteUserSentence(@PathVariable("sentence_id") Integer sentenceId, Authentication authentication) {
        sentenceService.deleteUserSentenceById(sentenceId, authentication.getName());
        return ResponseEntity.ok(null);
    }
}
