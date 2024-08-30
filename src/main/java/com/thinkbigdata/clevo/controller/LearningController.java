package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.LearningLogDto;
import com.thinkbigdata.clevo.dto.SentenceDto;
import com.thinkbigdata.clevo.service.LearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LearningController {
    private final LearningService learningService;

    @GetMapping("/learning/new-sentences")
    public ResponseEntity<List<SentenceDto>> getNewSentences(Authentication authentication) {
        List<SentenceDto> sentences = learningService.getNewSentences(authentication.getName());
        return ResponseEntity.ok(sentences);
    }

    @GetMapping ("/learning/sentence/{sentence_id}")
    public ResponseEntity<SentenceDto> getSentence(@PathVariable Integer sentence_id) {
        if (sentence_id == null || sentence_id <= 0)
            throw new RuntimeException("bad request");

        SentenceDto sentence = learningService.getSentence(sentence_id);
        return ResponseEntity.ok(sentence);
    }

//    @GetMapping("/learning/user-sentences")
//    public List<SentenceDto> getUserSentences(Authentication authentication) {
//
//    }

    @PostMapping("/v2/learning/score")
    public ResponseEntity<Float> getResult(@RequestBody @Valid SentenceDto sentence, Authentication authentication) {
        Float score = learningService.getResult(authentication.getName(), sentence);
        return ResponseEntity.ok(score);
    }
}
