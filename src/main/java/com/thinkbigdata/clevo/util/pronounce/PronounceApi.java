package com.thinkbigdata.clevo.util.pronounce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PronounceApi {
    private final RestTemplate restTemplate;
    @Value("${api.path}") private String PATH;
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

        String result = restTemplate.exchange(PATH, HttpMethod.POST, requestEntity, String.class).getBody();

        Map<String, Object> res = null;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            res = objectMapper.readValue(result, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if ((Integer) res.get("result") == -1 )
            throw new RuntimeException("API 호출 결과가 유효하지 않습니다.");

        Map<String, String> value = (Map<String, String>) res.get("return_object");

        return Double.valueOf(value.get("score"));
    }
}
