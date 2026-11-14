
package com.jobsfindai.backend.tailoring;

import com.jobsfindai.backend.dto.TailorResumeRequest;
import com.jobsfindai.backend.dto.TailoredResume;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/resumes")
public class ResumeTailoringController {

    private final ResumeTailoringService resumeTailoringService;

    public ResumeTailoringController(
            ResumeTailoringService resumeTailoringService
    ) {
        this.resumeTailoringService = resumeTailoringService;
    }

    @PostMapping("/{resumeId}/tailor")
    public TailoredResume tailorResume(
            @PathVariable Long resumeId,
            @Valid @RequestBody TailorResumeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {

        Long userId = Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );

        return resumeTailoringService.tailorResume(
                resumeId,
                userId,
                request.jobId()
        );
    }
}
