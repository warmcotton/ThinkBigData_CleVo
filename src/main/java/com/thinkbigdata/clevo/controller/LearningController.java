package com.thinkbigdata.clevo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkbigdata.clevo.dto.LearningLogDto;
import com.thinkbigdata.clevo.dto.SentenceDto;

import com.thinkbigdata.clevo.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LearningController {

    private static final Logger logger = LoggerFactory.getLogger(LearningController.class);

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

    @PostMapping ("/learning/score")
    public ResponseEntity<String> handleScoreRequest(@RequestBody Map<String, String> payload) {
        try {
            ResponseEntity<String> response = AudioController.handleAudioUpload(payload);
            logger.info("Received score from AudioController: " + response.getBody());

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e){
            logger.error("Error processing score request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
//    public ResponseEntity<LearningLogDto> submitRecord(Authentication authentication, @RequestPart Map<String, Integer> sentenceId, @RequestPart MultipartFile record) {
//        if (!sentenceId.containsKey("sentence_id"))
//            throw new RuntimeException("bad request");
//        if (sentenceId.get("sentence_id") == null)
//            throw new RuntimeException("bad request");
//
//        LearningLogDto score = learningService.getScore(authentication.getName(), sentenceId.get("sentence_id"), record);
//        return ResponseEntity.ok(score);
//    }

//    @GetMapping("/learning/user-sentences")
//    public List<SentenceDto> getUserSentences(Authentication authentication) {
//
//    }
}
