package com.jobsfindai.backend.dto;
import java.util.List;

public record ResumeProfile(
        String name,
        List<String> skills,
        String education,
        List<String> experience,
        String summary) {
}
