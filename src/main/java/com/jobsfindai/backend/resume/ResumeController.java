package com.jobsfindai.backend.resume;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ResumeController {

    @PostMapping("/api/resume/upload")
    public String uploadResume(@RequestPart("file")MultipartFile file){
        return "Uploaded: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)";
    }
}
