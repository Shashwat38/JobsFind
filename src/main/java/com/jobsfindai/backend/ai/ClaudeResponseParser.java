
package com.jobsfindai.backend.ai;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.jobsfindai.backend.dto.TailoredResume;


import java.util.ArrayList;
import java.util.List;

@Service
public class ClaudeResponseParser {

    private final ObjectMapper objectMapper;

    public ClaudeResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResumeProfile parseResponse(String response) {

        JsonNode root = objectMapper.readTree(response);
        JsonNode content = root.get("content");

        for (JsonNode block : content) {

            if ("text".equals(block.path("type").asString())) {

                String profileJson = block.path("text").asString();


                String cleanedJson = cleanJson(profileJson);

                return objectMapper.readValue(
                        cleanedJson,
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

                // Remove Markdown code fences if Claude includes them
                String cleanedJson = cleanJson(rankingJson);

                JsonNode rankings = objectMapper
                        .readTree(cleanedJson)
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

    private String cleanJson(String text) {

        String cleaned = text.trim();

        if (cleaned.startsWith("```")) {

            int firstNewline = cleaned.indexOf('\n');

            if (firstNewline != -1) {
                cleaned = cleaned.substring(firstNewline + 1);
            }

            cleaned = cleaned.trim();

            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(
                        0,
                        cleaned.length() - 3
                ).trim();
            }
        }

        return cleaned;
    }



    public TailoredResume parseTailoredResume(String response) {

        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException(
                    "Claude response cannot be empty"
            );
        }

        JsonNode root = objectMapper.readTree(response);
        JsonNode content = root.path("content");

        if (!content.isArray()) {
            throw new IllegalStateException(
                    "Claude response contains no content array"
            );
        }

        for (JsonNode block : content) {

            if ("text".equals(block.path("type").asString())) {

                String resumeJson = block.path("text").asString();

                String cleanedJson = cleanJson(resumeJson);

                return objectMapper.readValue(
                        cleanedJson,
                        TailoredResume.class
                );
            }
        }

        throw new IllegalStateException(
                "Claude response does not contain a tailored CV"
        );
    }


}
