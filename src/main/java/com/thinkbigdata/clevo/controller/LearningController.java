package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.service.LearningService;
import com.thinkbigdata.clevo.service.SentenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LearningController {
    private final LearningService learningService;
    private final SentenceService sentenceService;

    @PostMapping("learning/sentence/score")
    public ResponseEntity<LearningLogDto> getRandomSentenceResult(@RequestBody @Valid SentenceDto sentence, Authentication authentication) {
        LearningLogDto result = learningService.getRandomSentenceResult(authentication.getName(), sentence);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/learning/user-sentence/score")
    public ResponseEntity<LearningLogDto> getUserSentenceResult(@RequestBody @Valid UserSentenceDto sentence, Authentication authentication) {
        LearningLogDto result = learningService.getUserSentenceResult(authentication.getName(), sentence);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/learning/add")
    public ResponseEntity<?> addToUserSentence(Authentication authentication, @RequestBody Map<String, String> sentenceInfo) {
        if (!sentenceInfo.containsKey("sentence_id") || !sentenceInfo.containsKey("accuracy") || !sentenceInfo.containsKey("fluency")
                || !sentenceInfo.containsKey("total_score"))
            throw new RuntimeException("bad request");
        if (sentenceInfo.get("sentence_id") == null || sentenceInfo.get("accuracy") == null || sentenceInfo.get("fluency") == null
                || sentenceInfo.get("total_score") == null)
            throw new RuntimeException("bad request");

        sentenceService.addUserSentence(authentication.getName(), Integer.valueOf(sentenceInfo.get("sentence_id")), Double.valueOf(sentenceInfo.get("accuracy")),
                Double.valueOf(sentenceInfo.get("fluency")), Double.valueOf(sentenceInfo.get("total_score")));

        return ResponseEntity.ok(null);
    }
}
