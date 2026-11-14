package com.jobsfindai.backend.dto;



import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResumeProfileTest {

    @Test
    void shouldCreateResumeProfile() {
        ResumeProfile resumeProfile = new ResumeProfile(
                "Alex Johnson",
                List.of("Java", "Spring Boot", "AWS"),
                "BSc Computer Engineering",
                List.of("Backend Developer Intern"),
                "Backend Developer Intern"
        );

        assertEquals("Alex Johnson", resumeProfile.name());

        assertEquals(
                List.of("Java", "Spring Boot", "AWS"),
                resumeProfile.skills()
        );
    }
}
