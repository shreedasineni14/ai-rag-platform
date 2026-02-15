package com.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] generateEmbedding(String text) {

        try {
            String url = "http://localhost:11434/api/embeddings";

            Map<String, Object> request = new HashMap<>();
            request.put("model", "nomic-embed-text");
            request.put("prompt", text);

            String response = restTemplate.postForObject(url, request, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.get("embedding");

            float[] vector = new float[embeddingNode.size()];

            for (int i = 0; i < embeddingNode.size(); i++) {
                vector[i] = embeddingNode.get(i).floatValue();
            }

            return vector;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding from Ollama", e);
        }
    }

}
