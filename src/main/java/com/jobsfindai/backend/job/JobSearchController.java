
package com.jobsfindai.backend.job;

import com.jobsfindai.backend.dto.ResumeProfile;
import com.jobsfindai.backend.resume.ResumeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/jobs")
public class JobSearchController {

    private final JobSearchService jobSearchService;
    private final ResumeService resumeService;

    public JobSearchController(JobSearchService jobSearchService, ResumeService resumeService) {
        this.jobSearchService = jobSearchService;
        this.resumeService = resumeService;
    }

    @GetMapping("/search")
    public List<JobResult> searchJobs(
            @RequestParam String query,
            @RequestParam(required = false) String location
    ) {
        return jobSearchService.searchJobs(query, location);
    }

    @GetMapping("/recommendations/{resumeId}")
    public List<JobResult> getRecommendations(
            @PathVariable Long resumeId,
            @RequestParam(required = false) String location,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );

        ResumeProfile profile = resumeService.getResumeProfile(resumeId, userId);

        return jobSearchService.searchJobsForProfile(profile, location);
    }
}
