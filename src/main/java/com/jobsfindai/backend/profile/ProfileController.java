package com.jobsfindai.backend.profile;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @PostMapping("/api/profile")
    public String createProfile(@RequestBody UserProfile profile){
        return profileService.generateMessage(profile);
    }
}
