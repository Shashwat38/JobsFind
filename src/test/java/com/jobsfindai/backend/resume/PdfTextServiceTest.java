package com.jobsfindai.backend.resume;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfTextServiceTest {

    PdfTextService pdfTextService = new PdfTextService();

    @Test
    void shouldRejectInvalidPdf(){

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "This is not a PDF".getBytes()
        );

        assertThrows(IOException.class,
                () -> pdfTextService.extractText(file));
    }

    @Test
    void shouldExtractTextFromValidPdf() throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();

            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                contentStream.beginText();

                contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN),
                        12
                );

                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Java Spring Boot PostgreSQL");

                contentStream.endText();
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            document.save(output);

            byte[] pdfBytes = output.toByteArray();

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "resume.pdf",
                    "application/pdf",
                    pdfBytes
            );

            String text = pdfTextService.extractText(file);
            assertTrue(text.contains("Java Spring Boot PostgreSQL"));
        }

    }
}
