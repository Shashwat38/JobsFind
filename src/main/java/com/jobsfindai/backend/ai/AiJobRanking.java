
package com.jobsfindai.backend.ai;

import java.util.List;

public record AiJobRanking(
        String jobId,
        int score,
        List<String> missingRequirements,
        String reason
) {}
