package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    void shouldUplaodResumeSuccesfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Hello".getBytes()
        );

        mockMvc.perform(multipart("/api/resume/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Uploaded: resume.pdf (5 bytes)"));


            verify(resumeService).validateResume(any(MultipartFile.class));



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
                .validateResume(any(MultipartFile.class));

        mockMvc.perform(multipart("/api/resume/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid file"));
    }

}
