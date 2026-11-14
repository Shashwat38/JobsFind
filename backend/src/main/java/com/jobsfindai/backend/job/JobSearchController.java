
package com.jobsfindai.backend.job;

import com.jobsfindai.backend.dto.ResumeProfile;
import com.jobsfindai.backend.resume.ResumeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class JobSearchController {

    private final JobSearchService jobSearchService;
    private final ResumeService resumeService;
    private final JobRecommendationCacheService cacheService;

    public JobSearchController(
            JobSearchService jobSearchService,
            ResumeService resumeService,
            JobRecommendationCacheService cacheService
    ) {
        this.jobSearchService = jobSearchService;
        this.resumeService = resumeService;
        this.cacheService = cacheService;
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
        Long userId = getUserId(jwt);

        ResumeProfile profile =
                resumeService.getResumeProfile(resumeId, userId);

        String cacheLocation = normalizeLocation(location);

        Optional<List<JobResult>> cachedJobs =
                cacheService.getCachedJobs(resumeId, cacheLocation);

        if (cachedJobs.isPresent()) {
            return cachedJobs.get();
        }

        List<JobResult> jobs =
                jobSearchService.searchJobsForProfile(profile, location);

        cacheService.saveJobs(resumeId, cacheLocation, jobs);

        return jobs;
    }


    @GetMapping("/recommendations/{resumeId}/saved")
    public Optional<List<JobResult>> getSavedRecommendations(
            @PathVariable Long resumeId,
            @RequestParam(required = false) String location,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = getUserId(jwt);


        resumeService.getResumeProfile(resumeId, userId);

        String cacheLocation = normalizeLocation(location);

        return cacheService.getCachedJobs(resumeId, cacheLocation);
    }


    @PostMapping("/recommendations/{resumeId}/refresh")
    public List<JobResult> refreshRecommendations(
            @PathVariable Long resumeId,
            @RequestParam(required = false) String location,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = getUserId(jwt);

        ResumeProfile profile =
                resumeService.getResumeProfile(resumeId, userId);

        String cacheLocation = normalizeLocation(location);

        List<JobResult> jobs =
                jobSearchService.searchJobsForProfile(profile, location);

        cacheService.saveJobs(resumeId, cacheLocation, jobs);

        return jobs;
    }

    private Long getUserId(Jwt jwt) {
        return Long.parseLong(
                Objects.requireNonNull(jwt.getSubject())
        );
    }

    private String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            return "all";
        }

        return location.trim().toLowerCase(Locale.ROOT);
    }
}
