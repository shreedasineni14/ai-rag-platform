package com.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenerationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;   // Spring will inject this

    public String generateAnswer(String question, String context) {

        try {
            String url = "http://localhost:11434/api/generate";

            Map<String, Object> request = new HashMap<>();
            request.put("model", "llama3");
            request.put("prompt",
                    "Answer the question based only on the context below.\n\n" +
                    "Context:\n" + context +
                    "\n\nQuestion:\n" + question);
            request.put("stream", false);

            String response = restTemplate.postForObject(url, request, String.class);

            JsonNode root = objectMapper.readTree(response);
            return root.get("response").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate answer", e);
        }
    }
}
