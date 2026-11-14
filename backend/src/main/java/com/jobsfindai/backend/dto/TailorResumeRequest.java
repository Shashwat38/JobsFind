
package com.jobsfindai.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record TailorResumeRequest(
        @NotBlank String jobId
) {
}
