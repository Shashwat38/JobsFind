
package com.jobsfindai.backend.job;

import com.jobsfindai.backend.resume.Resume;
import com.jobsfindai.backend.resume.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class JobRecommendationCacheService {

    private final JobRecommendationCacheRepository cacheRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    public JobRecommendationCacheService(
            JobRecommendationCacheRepository cacheRepository,
            ResumeRepository resumeRepository,
            ObjectMapper objectMapper
    ) {
        this.cacheRepository = cacheRepository;
        this.resumeRepository = resumeRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Optional<List<JobResult>> getCachedJobs(
            Long resumeId,
            String location
    ) {
        return cacheRepository
                .findByResumeIdAndLocation(resumeId, location)
                .map(cache -> Arrays.asList(
                        objectMapper.readValue(
                                cache.getJobsJson(),
                                JobResult[].class
                        )
                ));
    }

    @Transactional
    public void saveJobs(
            Long resumeId,
            String location,
            List<JobResult> jobs
    ) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Resume not found"
                        )
                );

        String jobsJson = objectMapper.writeValueAsString(jobs);

        JobRecommendationCache cache = cacheRepository
                .findByResumeIdAndLocation(resumeId, location)
                .orElseGet(() ->
                        new JobRecommendationCache(
                                resume,
                                location,
                                jobsJson
                        )
                );

        cache.updateJobs(jobsJson);

        cacheRepository.save(cache);
    }
}
