package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.dto.ResumeProfile;
import com.jobsfindai.backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResumeControllerTest {

    ResumeService resumeService = mock(ResumeService.class);

    ResumeController resumeController = new ResumeController(resumeService);

    MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(resumeController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void shouldUploadResumeSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Hello".getBytes()
        );

        ResumeProfile profile = new ResumeProfile(
                "Alex Johnson",
                List.of("Java", "Spring Boot", "PostgreSQL"),
                "BSc Computer Engineering",
                List.of("Backend Developer Intern"),
                "Computer engineering student"
        );

        when(resumeService.processResume(any(MultipartFile.class)))
                .thenReturn(profile);

        mockMvc.perform(multipart("/api/resume/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alex Johnson"))
                .andExpect(jsonPath("$.skills[0]").value("Java"))
                .andExpect(jsonPath("$.education").value("BSc Computer Engineering"));


            verify(resumeService).processResume(any(MultipartFile.class));



    }

    @Test
    void shouldRejectInvalidUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Hello".getBytes()
        );

        doThrow(new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid file"
        ))
                .when(resumeService)
                .processResume(any(MultipartFile.class));

        mockMvc.perform(multipart("/api/resume/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid file"));
    }

    @Test
    void shouldReturnBadRequestWhenPdfCannotBeRead() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Hello".getBytes()
        );

        doThrow(new IOException(
                "Corrupted PDF"
        ))
                .when(resumeService)
                .processResume(any(MultipartFile.class));

        mockMvc.perform(multipart("/api/resume/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unable to read PDF file"));
    }

}
