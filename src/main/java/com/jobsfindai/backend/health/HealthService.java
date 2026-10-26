package com.jobsfindai.backend.health;

import org.springframework.stereotype.Service;

@Service
public class HealthService {


    public String getVersion(){
        return "JobsFindAI version 1.0";
    }

    public AppInfo getAppInfo(){
        return new AppInfo("JobsFindAI", "1.0", "running");
    }


}
