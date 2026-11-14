
package com.jobsfindai.backend.job;

import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;

import java.util.*;

import com.jobsfindai.backend.ai.ClaudeAiService;
import com.jobsfindai.backend.ai.AiJobRanking;
import java.util.Locale;

@Service
public class JobSearchService {

    private final RestClient restClient;
    private final JobMatchingService jobMatchingService;
    private final ClaudeAiService claudeAiService;
    private final MunicipalityService municipalityService;

    public JobSearchService(JobMatchingService jobMatchingService, ClaudeAiService claudeAiService, MunicipalityService municipalityService) {
        this.jobMatchingService = jobMatchingService;
        this.claudeAiService = claudeAiService;
        this.municipalityService = municipalityService;


        this.restClient = RestClient.builder()
                .baseUrl("https://jobsearch.api.jobtechdev.se")
                .build();
    }

    public List<JobResult> searchJobs(String query, String location) {

        String municipalityId = municipalityService.getMunicipalityId(location);

        JsonNode response = restClient.get()
                .uri(uriBuilder -> {

                    uriBuilder.path("/search")
                            .queryParam("q", query)
                            .queryParam("limit", 20);

                    if (municipalityId != null) {
                        uriBuilder.queryParam(
                                "municipality", municipalityId
                        );
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(JsonNode.class);

        List<JobResult> jobs = new ArrayList<>();

        if (response == null || !response.has("hits")) {
            return jobs;
        }

        for (JsonNode hit : response.path("hits")) {

            String id = hit.path("id").asText();
            String title = hit.path("headline").asText();

            String employer = hit.path("employer")
                    .path("name").asText();

            String jobLocation = hit.path("workplace_address")
                    .path("municipality").asText();

            String description = hit.path("description")
                    .path("text").asText();

            String applicationUrl = hit.path("webpage_url").asText();

            jobs.add(new JobResult(
                    id,
                    title,
                    employer,
                    jobLocation,
                    description,
                    applicationUrl,
                    0
            ));
        }

        return jobs;
    }

    public List<JobResult> searchJobsForProfile(ResumeProfile profile, String location) {

        if (profile.skills() == null || profile.skills().isEmpty()) {
            return List.of();
        }

        List<String> searchSkills = profile.skills().stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .distinct()
                .limit(3)
                .toList();

        Map<String, JobResult> uniqueJobs = new LinkedHashMap<>();

        for (String skill : searchSkills) {

            List<JobResult> results = searchJobs(skill, location);

            for (JobResult job : results) {
                uniqueJobs.putIfAbsent(job.id(), job);
            }
        }

        List<JobResult> top30 = jobMatchingService
                .rankJobs(profile, new ArrayList<>(uniqueJobs.values()))
                .stream()
                .limit(30)
                .toList();

        if (top30.isEmpty()) {
            return List.of();
        }

        List<AiJobRanking> aiRankings =
                claudeAiService.rankJobs(profile, top30);

        Map<String, Integer> scores = new HashMap<>();

        for (AiJobRanking ranking : aiRankings) {
            scores.put(ranking.jobId(), ranking.score());
        }

        return top30.stream()
                .filter(job -> scores.containsKey(job.id()))
                .map(job -> new JobResult(
                        job.id(),
                        job.title(),
                        job.employer(),
                        job.location(),
                        job.description(),
                        job.applicationUrl(),
                        scores.get(job.id())
                ))
                .sorted(Comparator.comparingInt(
                        JobResult::matchScore
                ).reversed())
                .toList();
    }


    public JobResult getJobById(String jobId) {

        if (jobId == null || jobId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job ID cannot be empty"
            );
        }

        JsonNode job = restClient.get()
                .uri("/ad/{id}", jobId)
                .retrieve()
                .body(JsonNode.class);

        if (job == null || job.isMissingNode()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Job not found"
            );
        }

        return new JobResult(
                job.path("id").asString(),
                job.path("headline").asString(),
                job.path("employer").path("name").asString(),
                job.path("workplace_address")
                        .path("municipality").asString(),
                job.path("description").path("text").asString(),
                job.path("webpage_url").asString(),
                0
        );
    }



}
