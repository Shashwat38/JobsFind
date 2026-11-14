package com.jobsfindai.backend.profile;

import jakarta.validation.constraints.NotBlank;

public record UserProfile(@NotBlank String name, @NotBlank String skill){



}
