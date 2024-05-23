package com.thinkbigdata.clevo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AudioController {

    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);

    @PostMapping("/api/upload-audio")
    public ResponseEntity<String> handleAudioUpload(@RequestBody Map<String, String> payload) {
        try {
            String base64Audio = payload.get("audio");
            logger.info("Received audio data from frontend");

            // Base64 데이터를 API 호출
            String result = sendAudioToApi(base64Audio);
            logger.info("API call result: " + result);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error processing audio upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    private String sendAudioToApi(String base64Audio) {
        String url = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "3d31743b-40d0-4465-ac0b-fc7190feff65");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("request_id", "reserved field");
        Map<String, String> argument = new HashMap<>();
        argument.put("language_code", "english");
        // "script" 옵션은 추후 상의 후 결정 예정
        // argument.put("script", "PRONUNCIATION_SCRIPT");
        argument.put("audio", base64Audio);
        body.put("argument", argument);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        logger.info("Sending API request with body: " + body);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        logger.info("Received response from API: " + response.getBody());

        return response.getBody();
    }
}
