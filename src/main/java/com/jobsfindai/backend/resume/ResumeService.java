package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.ai.ClaudeAiService;
import com.jobsfindai.backend.dto.ResumeProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.jobsfindai.backend.dto.ResumeProfile;

import java.io.IOException;


@Service
public class ResumeService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final PdfTextService pdfTextService;
    private final ClaudeAiService claudeAiService;

    public ResumeService(PdfTextService pdfTextService, ClaudeAiService claudeAiService){

        this.pdfTextService = pdfTextService;
        this.claudeAiService = claudeAiService;
    }


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

    public ResumeProfile processResume(MultipartFile file) throws IOException {
        validateResume(file);
        String resumeText = pdfTextService.extractText(file);
        return claudeAiService.analyzeResume(resumeText);

    }
}
