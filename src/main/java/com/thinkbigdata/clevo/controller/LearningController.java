package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.SentenceDto;
import com.thinkbigdata.clevo.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @GetMapping("/learning/user-sentences")
//    public List<SentenceDto> getUserSentences(Authentication authentication) {
//
//    }
}
