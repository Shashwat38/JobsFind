package com.jobsfindai.backend.resume;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextService {


    public String extractText(MultipartFile file) throws IOException {
        byte[] pdfBytes = file.getBytes();

        try (PDDocument document = Loader.loadPDF(pdfBytes)){

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return text;
        }



    }
}
