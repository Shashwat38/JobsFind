package com.jobsfindai.backend.resume;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class ResumeServiceTest {

    ResumeService resumeService = new ResumeService();



    @Test
    void shouldRejectEmptyFile(){

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
    void shouldAcceptValidFile(){

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
}
