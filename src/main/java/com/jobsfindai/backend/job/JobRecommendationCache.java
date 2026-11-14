
package com.jobsfindai.backend.job;

import com.jobsfindai.backend.resume.Resume;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_recommendation_cache",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"resume_id", "location"}
        )
)
public class JobRecommendationCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "jobs_json", nullable = false, columnDefinition = "TEXT")
    private String jobsJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected JobRecommendationCache() {
    }

    public JobRecommendationCache(
            Resume resume,
            String location,
            String jobsJson
    ) {
        this.resume = resume;
        this.location = location;
        this.jobsJson = jobsJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Resume getResume() {
        return resume;
    }

    public String getLocation() {
        return location;
    }

    public String getJobsJson() {
        return jobsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateJobs(String jobsJson) {
        this.jobsJson = jobsJson;
        this.createdAt = LocalDateTime.now();
    }
}
