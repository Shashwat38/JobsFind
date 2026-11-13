package com.jobsfindai.backend.ai;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

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

    public List<AiJobRanking> parseJobRankings(String response) {

        JsonNode root = objectMapper.readTree(response);
        JsonNode content = root.get("content");

        for (JsonNode block : content) {

            if ("text".equals(block.path("type").asString())) {

                String rankingJson = block.path("text").asString();

                JsonNode rankings = objectMapper
                        .readTree(rankingJson)
                        .path("rankings");

                if (!rankings.isArray()) {
                    throw new IllegalStateException(
                            "Claude response has no rankings array"
                    );
                }

                List<AiJobRanking> results = new ArrayList<>();

                for (JsonNode ranking : rankings) {

                    List<String> missingRequirements = new ArrayList<>();

                    for (JsonNode requirement :
                            ranking.path("missingRequirements")) {

                        missingRequirements.add(requirement.asString());
                    }

                    results.add(new AiJobRanking(
                            ranking.path("jobId").asString(),
                            ranking.path("score").asInt(),
                            missingRequirements,
                            ranking.path("reason").asString()
                    ));
                }

                return results;
            }
        }

        throw new IllegalStateException(
                "Claude response does not contain job rankings"
        );
    }


}
