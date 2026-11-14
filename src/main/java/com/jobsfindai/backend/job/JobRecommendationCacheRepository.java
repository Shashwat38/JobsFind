
package com.jobsfindai.backend.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRecommendationCacheRepository
        extends JpaRepository<JobRecommendationCache, Long> {

    Optional<JobRecommendationCache> findByResumeIdAndLocation(
            Long resumeId,
            String location
    );
}
