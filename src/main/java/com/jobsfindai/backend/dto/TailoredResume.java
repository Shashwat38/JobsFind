
package com.jobsfindai.backend.dto;

import java.util.List;

public record TailoredResume(
        String name,
        String email,
        String phone,
        String location,
        List<TailoredEducation> education,
        List<TailoredExperience> workExperience,
        List<TailoredProject> projects,
        List<String> technicalSkills
) {
}
