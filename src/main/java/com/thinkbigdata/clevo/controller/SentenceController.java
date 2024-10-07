package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.entity.Sentence;
import com.thinkbigdata.clevo.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/sentence/user")
    public ResponseEntity<?> addToUserSentence(Authentication authentication, @RequestBody Map<String, String> sentenceInfo) {
        if (!sentenceInfo.containsKey("sentence_id")) throw new RuntimeException("bad request");
        if (sentenceInfo.get("sentence_id") == null) throw new RuntimeException("bad request");

        sentenceService.addUserSentence(authentication.getName(), Integer.valueOf(sentenceInfo.get("sentence_id")));

        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/sentence/user/{sentence_id}")
    public ResponseEntity<?> deleteUserSentence(@PathVariable("sentence_id") Integer sentenceId, Authentication authentication) {
        sentenceService.deleteUserSentenceById(sentenceId, authentication.getName());
        return ResponseEntity.ok(null);
    }
}
