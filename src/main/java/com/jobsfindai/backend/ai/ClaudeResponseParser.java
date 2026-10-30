package com.jobsfindai.backend.ai;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class ClaudeResponseParser {

    private final ObjectMapper objectMapper;

    public ClaudeResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResumeProfile parseResponse(String response){


        JsonNode root = objectMapper.readTree(response);

        JsonNode content = root.get("content");

        for (JsonNode block : content) {

            if ("text".equals(block.get("type").asString())) {

                String profileJson = block.get("text").asString();

                return objectMapper.readValue(
                        profileJson,
                        ResumeProfile.class
                );
            }
        }

        throw new IllegalStateException(
                "Claude response does not contain a text block"
        );
    }
}
