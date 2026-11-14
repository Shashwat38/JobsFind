package com.jobsfindai.backend.job;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/municipalities")
public class MunicipalityController {

    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    @GetMapping
    public List<String> getMunicipalities() {
        List<String> result = new ArrayList<>();
        result.add("");
        result.addAll(municipalityService.getMunicipalities());
        return result;
    }
}