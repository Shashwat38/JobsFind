
package com.jobsfindai.backend.document;

import com.jobsfindai.backend.dto.TailoredResume;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
public class TailoredResumeDocumentController {

    private final TailoredResumeWordService wordService;
    private final TailoredResumePdfService pdfService;

    public TailoredResumeDocumentController(
            TailoredResumeWordService wordService,
            TailoredResumePdfService pdfService
    ) {
        this.wordService = wordService;
        this.pdfService = pdfService;
    }

    @PostMapping("/word")
    public ResponseEntity<byte[]> downloadWord(
            @Valid @RequestBody TailoredResume resume
    ) throws IOException {

        byte[] document = wordService.generateWord(resume);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"tailored-resume.docx\""
                )
                .body(document);
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @Valid @RequestBody TailoredResume resume
    ) {
        byte[] document = pdfService.generatePdf(resume);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"tailored-resume.pdf\""
                )
                .body(document);
    }
}
