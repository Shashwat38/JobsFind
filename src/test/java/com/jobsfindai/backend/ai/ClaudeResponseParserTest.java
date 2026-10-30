package com.jobsfindai.backend.ai;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClaudeResponseParserTest {

    @Test
    void shouldParseClaudeResponse() {

        ObjectMapper objectMapper = new ObjectMapper();

        ClaudeResponseParser claudeResponseParser = new ClaudeResponseParser(objectMapper);

        String response = """
        {
          "content": [
            {
              "type": "text",
              "text": "{\\"name\\":\\"Alex Johnson\\",\\"skills\\":[\\"Java\\",\\"AWS\\"],\\"education\\":\\"BSc Computer Engineering\\",\\"experience\\":[],\\"summary\\":\\"Computer engineering student\\"}"
            }
          ]
        }
        """;

        ResumeProfile resumeProfile = claudeResponseParser.parseResponse(response);

        assertEquals("Alex Johnson", resumeProfile.name());

        assertEquals(
                List.of("Java", "AWS"),
                resumeProfile.skills()
        );

        assertEquals(
                "BSc Computer Engineering",
                resumeProfile.education()
        );

        assertEquals(
                List.of(),
                resumeProfile.experience()
        );

        assertEquals(
                "Computer engineering student",
                resumeProfile.summary()
        );
    }
}
