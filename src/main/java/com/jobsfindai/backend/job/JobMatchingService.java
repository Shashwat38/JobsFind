
package com.jobsfindai.backend.job;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class JobMatchingService {

    public List<JobResult> rankJobs(
            ResumeProfile profile,
            List<JobResult> jobs
    ) {
        List<String> skills = profile.skills() == null
                ? List.of()
                : profile.skills();

        return jobs.stream()
                .map(job -> scoreJob(job, skills))
                .sorted(Comparator.comparingInt(
                        JobResult::matchScore
                ).reversed())
                .toList();
    }

    private JobResult scoreJob(
            JobResult job,
            List<String> skills
    ) {
        String jobText = (
                (job.title() == null ? "" : job.title()) + " " +
                        (job.description() == null ? "" : job.description())
        ).toLowerCase(Locale.ROOT);

        long matchingSkills = skills.stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .filter(skill -> jobText.contains(
                        skill.toLowerCase(Locale.ROOT)
                ))
                .count();

        int score = skills.isEmpty()
                ? 0
                : (int) (100.0 * matchingSkills / skills.size());

        return new JobResult(
                job.id(),
                job.title(),
                job.employer(),
                job.location(),
                job.description(),
                job.applicationUrl(),
                score
        );
    }
}
