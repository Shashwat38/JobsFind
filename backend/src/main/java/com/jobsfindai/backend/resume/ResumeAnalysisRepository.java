package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.resume.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeAnalysisRepository
        extends JpaRepository<ResumeAnalysis, Long> {

    Optional<ResumeAnalysis> findByResumeId(Long resumeId);
}
