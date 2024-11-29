package com.thinkbigdata.clevo.util.pronounce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thinkbigdata.clevo.dto.sentence.SentenceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static com.thinkbigdata.clevo.util.pronounce.PronunciationEvaluator.Result;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class PronounceApi {
    private final RestTemplate restTemplate;
    private final PronunciationEvaluator pronunciationEvaluator;
    @Value("${pron.path}") private String P_PATH;
    @Value("${recog.path}") private String R_PATH;
    @Value("${api.key}") private String accessKey;

    public Double getSentenceScore(String eng, String base64) {
        ObjectNode requests = JsonNodeFactory.instance.objectNode();
        ObjectNode arguments = JsonNodeFactory.instance.objectNode();

        arguments.put("language_code", "english");
        arguments.put("script", eng);
        arguments.put("audio", base64);
        requests.put("argument", arguments);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessKey);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(requests, headers);

        String result = restTemplate.exchange(P_PATH, HttpMethod.POST, requestEntity, String.class).getBody();

        Map<String, Object> res = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.readValue(result, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

        if ((Integer) res.get("result") == -1 )
            throw new RestClientException("API 호출 결과가 유효하지 않습니다.");

        Map<String, String> value = (Map<String, String>) res.get("return_object");

        return Double.valueOf(value.get("score"));
    }

    public String getSentenceScript(String base64) {
        ObjectNode requests = JsonNodeFactory.instance.objectNode();
        ObjectNode arguments = JsonNodeFactory.instance.objectNode();

        arguments.put("language_code", "english");
        arguments.put("audio", base64);
        requests.put("argument", arguments);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessKey);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(requests, headers);

        String result = restTemplate.exchange(R_PATH, HttpMethod.POST, requestEntity, String.class).getBody();

        Map<String, Object> res = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.readValue(result, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

        if ((Integer) res.get("result") == -1 )
            throw new RestClientException("API 호출 결과가 유효하지 않습니다.");

        Map<String, String> value = (Map<String, String>) res.get("return_object");

        return value.get("recognized");
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Result> getSentenceScriptV2(SentenceDto sentence)  {
        ObjectNode requests = JsonNodeFactory.instance.objectNode();
        ObjectNode arguments = JsonNodeFactory.instance.objectNode();

        arguments.put("language_code", "english");
        arguments.put("audio", sentence.getBase64());
        requests.put("argument", arguments);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessKey);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(requests, headers);

        String result = restTemplate.exchange(R_PATH, HttpMethod.POST, requestEntity, String.class).getBody();

        Map<String, Object> res = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.readValue(result, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

        if ((Integer) res.get("result") == -1 )
            throw new RestClientException("API 호출 결과가 유효하지 않습니다.");

        Map<String, String> value = (Map<String, String>) res.get("return_object");

        String recognized = value.get("recognized");
        return CompletableFuture.completedFuture(pronunciationEvaluator.evaluatePronunciation(recognized, sentence.getEng()));
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Double> getSentenceScoreV2(SentenceDto sentence) {
        ObjectNode requests = JsonNodeFactory.instance.objectNode();
        ObjectNode arguments = JsonNodeFactory.instance.objectNode();

        arguments.put("language_code", "english");
        arguments.put("script", sentence.getEng());
        arguments.put("audio", sentence.getBase64());
        requests.put("argument", arguments);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessKey);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(requests, headers);

        String result = restTemplate.exchange(P_PATH, HttpMethod.POST, requestEntity, String.class).getBody();

        Map<String, Object> res = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.readValue(result, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

        if ((Integer) res.get("result") == -1 )
            throw new RestClientException("API 호출 결과가 유효하지 않습니다.");

        Map<String, String> value = (Map<String, String>) res.get("return_object");

        return CompletableFuture.completedFuture(Double.valueOf(value.get("score")));
    }
}
