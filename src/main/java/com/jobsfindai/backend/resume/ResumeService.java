package com.jobsfindai.backend.resume;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ResumeService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;


    public void validateResume(MultipartFile file){

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE){
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "File exceeds 5 MB"
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null || !filename.toLowerCase().endsWith(".pdf") || !"application/pdf".equals(file.getContentType())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The uploaded file was not a pdf"
            );
        }

    }
}
