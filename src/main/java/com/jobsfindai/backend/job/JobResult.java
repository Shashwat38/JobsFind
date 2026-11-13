
package com.jobsfindai.backend.job;

public record JobResult(
        String id,
        String title,
        String employer,
        String location,
        String description,
        String applicationUrl,
        int matchScore
) {}
