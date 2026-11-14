package com.jobsfindai.backend.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController

public class HealthController {

    private final HealthService healthService;


    public HealthController (HealthService healthService){
        this.healthService = healthService;
    }

    @GetMapping("/api/health")
    public String response(){
        return "JobsFindAI backend is running";
    }

    @GetMapping("/api/version")
    public String getVersion(){
        return healthService.getVersion();
    }

    @GetMapping("/api/info")
    public AppInfo appInfo(){
        return healthService.getAppInfo();
    }

}
