package com.jobsfindai.backend.resume;


import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/api/resume/upload")
    public ResumeProfile uploadResume(@RequestPart("file")MultipartFile file) throws IOException {

        return resumeService.processResume(file);

    }
}
