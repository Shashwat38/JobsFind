package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.ai.ClaudeAiService;
import com.jobsfindai.backend.ai.ClaudeResponseParser;
import com.jobsfindai.backend.resume.ResumeRepository;
import com.jobsfindai.backend.dto.ResumeProfile;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



public class ResumeServiceTest {

    ResumeRepository resumeRepository = mock(ResumeRepository.class);
    ResumeService resumeService = new ResumeService(
            new PdfTextService(),
            new ClaudeAiService("test-key", new ClaudeResponseParser(new ObjectMapper())),
            resumeRepository
    );




    @Test
    void shouldRejectEmptyFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "".getBytes()

        );


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> resumeService.validateResume(file)
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void shouldAcceptValidFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Hi".getBytes()

        );
        assertDoesNotThrow(
                () -> resumeService.validateResume(file)
        );
    }

    @Test
    void shouldProcessResumeSuccessfully() throws IOException {
        PdfTextService pdfTextService = mock(PdfTextService.class);

        ClaudeAiService claudeAiService = mock(ClaudeAiService.class);

        ResumeRepository resumeRepository = mock(ResumeRepository.class);

        ResumeService resumeService = new ResumeService(pdfTextService, claudeAiService, resumeRepository);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Hello".getBytes()

        );

        ResumeProfile profile = new ResumeProfile(
                "Alex Johnson",
                List.of("Java", "Spring Boot", "AWS"),
                "BSc Computer Engineering",
                List.of("Backend Developer Intern"),
                "Computer engineering student"
        );
        when(pdfTextService.extractText(file))
                .thenReturn("Java Spring Boot AWS");

        when(claudeAiService.analyzeResume("Java Spring Boot AWS"))
                .thenReturn(profile);

        ResumeProfile result = resumeService.processResume(file);

        assertEquals("Alex Johnson", result.name());
        assertEquals(
                List.of("Java", "Spring Boot", "AWS"),
                result.skills()
        );
        assertEquals("BSc Computer Engineering", result.education());
        assertEquals(
                List.of("Backend Developer Intern"),
                result.experience()
        );
        assertEquals("Computer engineering student", result.summary());

        verify(pdfTextService).extractText(file);
        verify(claudeAiService).analyzeResume("Java Spring Boot AWS");
    }
}
