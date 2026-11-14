
package com.jobsfindai.backend.dto;

import java.util.List;

public record TailoredExperience(
        String title,
        String company,
        String dates,
        List<String> bullets
) {
}
