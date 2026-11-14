
package com.jobsfindai.backend.job;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobMatchingServiceTest {

    private final JobMatchingService service =
            new JobMatchingService();

    @Test
    void shouldRankJobsByMatchingSkills() {

        List<String> skills = List.of(
                "Java", "Spring Boot", "PostgreSQL"
        );

        JobResult strongMatch = new JobResult(
                "1",
                "Java Developer",
                "Company A",
                "Stockholm",
                "Java Spring Boot PostgreSQL",
                "https://example.com/1",
                0
        );

        JobResult weakMatch = new JobResult(
                "2",
                "Frontend Developer",
                "Company B",
                "Stockholm",
                "React and CSS",
                "https://example.com/2",
                0
        );

        // Test the scoring logic using the profile's skills.
        // Replace this with a ResumeProfile instance
        // matching your actual DTO constructor.
    }
}
