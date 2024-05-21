package com.thinkbigdata.clevo.util.pronounce;

import com.fasterxml.jackson.databind.JsonNode;
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

    public Map<String, String> requestToServer(ObjectNode request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessKey);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<Map<String, String>> result = restTemplate.exchange(PATH, HttpMethod.POST, requestEntity,
                    new ParameterizedTypeReference<Map<String, String>>() {});

        return result.getBody();
    }
}
