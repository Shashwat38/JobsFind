package com.jobsfindai.backend.profile;

import org.springframework.stereotype.Service;

@Service
public class ProfileService {


    public String generateMessage(UserProfile userProfile){
        return "Hello " + userProfile.getName() + ", you main skill is " + userProfile.getSkill();
    }
}
