
package com.jobsfindai.backend.document;

import com.jobsfindai.backend.dto.TailoredEducation;
import com.jobsfindai.backend.dto.TailoredExperience;
import com.jobsfindai.backend.dto.TailoredProject;
import com.jobsfindai.backend.dto.TailoredResume;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class TailoredResumePdfService {

    private static final Font NAME_FONT =
            new Font(Font.HELVETICA, 18, Font.BOLD);

    private static final Font HEADING_FONT =
            new Font(Font.HELVETICA, 10, Font.BOLD);

    private static final Font TITLE_FONT =
            new Font(Font.HELVETICA, 10.5f, Font.BOLD);

    private static final Font BODY_FONT =
            new Font(Font.HELVETICA, 10.5f, Font.NORMAL);

    public byte[] generatePdf(TailoredResume resume) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Document document = new Document(
                PageSize.A4,
                52,
                52,
                42,
                42
        );

        try {
            PdfWriter.getInstance(document, output);
            document.open();

            Paragraph name = new Paragraph(
                    safe(resume.name()),
                    NAME_FONT
            );
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingAfter(2);
            document.add(name);

            Paragraph contact = new Paragraph(
                    joinNonBlank(
                            resume.location(),
                            resume.email(),
                            resume.phone()
                    ),
                    BODY_FONT
            );
            contact.setAlignment(Element.ALIGN_CENTER);
            contact.setSpacingAfter(14);
            document.add(contact);

            if (hasItems(resume.education())) {
                addHeading(document, "EDUCATION");

                for (TailoredEducation education : resume.education()) {
                    addEntry(
                            document,
                            education.degree(),
                            education.graduationDate(),
                            education.institution()
                    );
                }
            }

            if (hasItems(resume.workExperience())) {
                addHeading(document, "WORK EXPERIENCE");

                for (TailoredExperience experience : resume.workExperience()) {
                    addEntry(
                            document,
                            experience.title(),
                            experience.dates(),
                            experience.company()
                    );

                    addBullets(document, experience.bullets());
                }
            }

            if (hasItems(resume.projects())) {
                addHeading(document, "PROJECTS");

                for (TailoredProject project : resume.projects()) {
                    Paragraph title = new Paragraph(
                            safe(project.name()),
                            TITLE_FONT
                    );
                    title.setSpacingBefore(5);
                    title.setSpacingAfter(3);
                    title.setKeepTogether(true);
                    document.add(title);

                    addBullets(document, project.bullets());
                }
            }

            if (hasItems(resume.technicalSkills())) {
                addHeading(document, "TECHNICAL SKILLS");

                Paragraph skills = new Paragraph(
                        String.join(" | ", resume.technicalSkills()),
                        BODY_FONT
                );
                skills.setSpacingAfter(2);
                document.add(skills);
            }

        } catch (DocumentException exception) {
            throw new IllegalStateException(
                    "Could not generate PDF",
                    exception
            );
        } finally {
            document.close();
        }

        return output.toByteArray();
    }

    private void addHeading(
            Document document,
            String text
    ) throws DocumentException {

        Paragraph heading = new Paragraph(text, HEADING_FONT);
        heading.setSpacingBefore(12);
        heading.setSpacingAfter(5);

        document.add(heading);

        LineSeparator line = new LineSeparator();
        line.setLineWidth(0.6f);
        document.add(line);
    }

    private void addEntry(
            Document document,
            String title,
            String date,
            String subtitle
    ) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2});
        table.setSpacingBefore(5);
        table.setSpacingAfter(2);
        table.setKeepTogether(true);

        PdfPCell titleCell = new PdfPCell(
                new Phrase(safe(title), TITLE_FONT)
        );
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(0);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell dateCell = new PdfPCell(
                new Phrase(safe(date), BODY_FONT)
        );
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setPadding(0);
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(titleCell);
        table.addCell(dateCell);

        document.add(table);

        if (subtitle != null && !subtitle.isBlank()) {
            Paragraph sub = new Paragraph(
                    subtitle,
                    BODY_FONT
            );
            sub.setSpacingAfter(3);
            document.add(sub);
        }
    }

    private void addBullets(
            Document document,
            List<String> bullets
    ) throws DocumentException {

        if (bullets == null || bullets.isEmpty()) {
            return;
        }

        com.lowagie.text.List list =
                new com.lowagie.text.List(
                        com.lowagie.text.List.UNORDERED
                );

        list.setIndentationLeft(15);
        list.setSymbolIndent(8);

        for (String bullet : bullets) {
            if (bullet == null || bullet.isBlank()) {
                continue;
            }

            ListItem item = new ListItem(bullet, BODY_FONT);
            item.setSpacingAfter(2);
            list.add(item);
        }

        document.add(list);
    }

    private boolean hasItems(List<?> items) {
        return items != null && !items.isEmpty();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String joinNonBlank(String... values) {
        StringBuilder result = new StringBuilder();

        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }

            if (!result.isEmpty()) {
                result.append(" | ");
            }

            result.append(value);
        }

        return result.toString();
    }
}
