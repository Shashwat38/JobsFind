
package com.jobsfindai.backend.resume;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextService {

    public String extractText(MultipartFile file) throws IOException {
        return extractText(file.getBytes());
    }

    public String extractText(Resource resource) throws IOException {
        try (var inputStream = resource.getInputStream()) {
            return extractText(inputStream.readAllBytes());
        }
    }

    private String extractText(byte[] pdfBytes) throws IOException {

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        }
    }
}
