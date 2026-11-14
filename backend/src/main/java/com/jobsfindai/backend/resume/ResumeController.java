package com.jobsfindai.backend.resume;


import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.util.List;
import java.util.Objects;


@RestController
public class ResumeController {

    private final ResumeService resumeService;
    private final FileStorageService fileStorageService;

    public ResumeController(ResumeService resumeService, FileStorageService fileStorageService) {

        this.resumeService = resumeService;
        this.fileStorageService = fileStorageService;

    }

    @PostMapping("/api/resume/upload")
    public ResumeProfile uploadResume(@RequestPart("file")MultipartFile file,
                                      @AuthenticationPrincipal Jwt jwt)
            throws IOException {

                Long userId = Long.parseLong(
                        Objects.requireNonNull(jwt.getSubject())
                );

                return resumeService.processResume(file, userId);
    }

    @GetMapping("/api/resumes")
    public List<ResumeResponse> getMyResumes(@AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );

        return resumeService.getResumeResponsesForUser(userId);
    }


    @GetMapping("/api/resumes/{id}/download")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {

        Long userId = Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );

        Resume resume = resumeService.getResumeForUser(id, userId);

        Resource resource = fileStorageService.load(resume.getStorageKey());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "PDF file not found"
            );
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(resume.getFilename())
                                .build()
                                .toString()
                )
                .body(resource);
    }


    @DeleteMapping("/api/resumes/{id}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {

        Long userId = Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );

        resumeService.deleteResume(id, userId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/api/resumes/{id}/analysis",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getResumeAnalysis(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );

        String analysis = resumeService.getResumeAnalysis(id, userId);

        return ResponseEntity.ok(analysis);
    }

}

