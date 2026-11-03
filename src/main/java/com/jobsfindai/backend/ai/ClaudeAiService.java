package com.jobsfindai.backend.ai;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;

@Service
public class ClaudeAiService {

    private final String apiKey;
    private final RestClient restClient;
    private final ClaudeResponseParser claudeResponseParser;

    public ClaudeAiService(@Value("${anthropic.api.key}") String apiKey, ClaudeResponseParser claudeResponseParser){
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().baseUrl("https://api.anthropic.com").build();
        this.claudeResponseParser = claudeResponseParser;
    }

    public ResumeProfile analyzeResume(String resumeText){

        String prompt = """
        Analyze the following CV.

        Return only a valid JSON object with these fields:
        name: string
        skills: array of strings
        education: string
        experience: array of strings
        summary: string

        Do not include Markdown or explanations.
        Use empty strings or empty arrays when information is missing.
        Do not invent information.

        CV:
        """ + resumeText;

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = Map.of(
                "model", "claude-haiku-5-5",
                "max_tokens", 1024,
                "messages", List.of(message)
        );

        String response = restClient.post()
                .uri("/v1/messages")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);


        return claudeResponseParser.parseResponse(response);

    }
}
