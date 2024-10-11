package com.thinkbigdata.clevo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkbigdata.clevo.dto.sentence.LearningLogDto;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import com.thinkbigdata.clevo.dto.sentence.UserSentenceDto;
import com.thinkbigdata.clevo.exception.InsufficientUserInfoException;
import com.thinkbigdata.clevo.service.LearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LearningController {
    private final LearningService learningService;

    @PostMapping("learning/sentence/score")
    public ResponseEntity<LearningLogDto> getRandomSentenceResult(@RequestBody @Valid SentenceDto sentence, Authentication authentication) throws JsonProcessingException, InsufficientUserInfoException {
        LearningLogDto result = learningService.getRandomSentenceResult(authentication.getName(), sentence);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/learning/user-sentence/score")
    public ResponseEntity<LearningLogDto> getUserSentenceResult(@RequestBody @Valid UserSentenceDto sentence, Authentication authentication) throws JsonProcessingException, InsufficientUserInfoException {
        LearningLogDto result = learningService.getUserSentenceResult(authentication.getName(), sentence);
        return ResponseEntity.ok(result);
    }
}
