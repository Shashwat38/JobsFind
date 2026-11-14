
package com.jobsfindai.backend.tailoring;

import com.jobsfindai.backend.ai.ClaudeAiService;
import com.jobsfindai.backend.dto.TailoredResume;
import com.jobsfindai.backend.job.JobResult;
import com.jobsfindai.backend.job.JobSearchService;
import com.jobsfindai.backend.resume.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class ResumeTailoringService {

    private final ResumeService resumeService;
    private final JobSearchService jobSearchService;
    private final ClaudeAiService claudeAiService;

    public ResumeTailoringService(
            ResumeService resumeService,
            JobSearchService jobSearchService,
            ClaudeAiService claudeAiService
    ) {
        this.resumeService = resumeService;
        this.jobSearchService = jobSearchService;
        this.claudeAiService = claudeAiService;
    }

    public TailoredResume tailorResume(
            Long resumeId,
            Long userId,
            String jobId
    ) throws IOException {

        String resumeText = resumeService.getResumeText(
                resumeId,
                userId
        );

        JobResult job = jobSearchService.getJobById(jobId);

        if (job.description() == null ||
                job.description().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The selected job has no description"
            );
        }

        return claudeAiService.tailorResume(
                resumeText,
                job.description()
        );
    }
}
