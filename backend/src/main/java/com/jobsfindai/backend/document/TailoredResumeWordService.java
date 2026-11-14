
package com.jobsfindai.backend.document;

import com.jobsfindai.backend.dto.TailoredEducation;
import com.jobsfindai.backend.dto.TailoredExperience;
import com.jobsfindai.backend.dto.TailoredProject;
import com.jobsfindai.backend.dto.TailoredResume;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TailoredResumeWordService {

    private static final int BODY_FONT_SIZE = 21;

    public byte[] generateWord(TailoredResume resume) throws IOException {
        try (
                XWPFDocument document = new XWPFDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            configurePage(document);

            XWPFParagraph name = document.createParagraph();
            formatParagraph(name, 0, 2);
            name.setAlignment(ParagraphAlignment.CENTER);
            addText(name, resume.name(), 36, true);

            XWPFParagraph contact = document.createParagraph();
            formatParagraph(contact, 0, 12);
            contact.setAlignment(ParagraphAlignment.CENTER);

            addText(
                    contact,
                    joinNonBlank(
                            resume.location(),
                            resume.email(),
                            resume.phone()
                    ),
                    BODY_FONT_SIZE,
                    false
            );

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
                    XWPFParagraph title = document.createParagraph();
                    formatParagraph(title, 5, 2);
                    keepWithNext(title);

                    addText(
                            title,
                            project.name(),
                            BODY_FONT_SIZE,
                            true
                    );

                    addBullets(document, project.bullets());
                }
            }

            if (hasItems(resume.technicalSkills())) {
                addHeading(document, "TECHNICAL SKILLS");

                XWPFParagraph skills = document.createParagraph();
                formatParagraph(skills, 0, 0);

                addText(
                        skills,
                        String.join(" | ", resume.technicalSkills()),
                        BODY_FONT_SIZE,
                        false
                );
            }

            document.write(output);
            return output.toByteArray();
        }
    }

    private void configurePage(XWPFDocument document) {
        CTSectPr section = document.getDocument()
                .getBody().isSetSectPr()
                ? document.getDocument().getBody().getSectPr()
                : document.getDocument().getBody().addNewSectPr();

        CTPageSz pageSize = section.isSetPgSz()
                ? section.getPgSz()
                : section.addNewPgSz();

        pageSize.setW(BigInteger.valueOf(11906));
        pageSize.setH(BigInteger.valueOf(16838));

        CTPageMar margins = section.isSetPgMar()
                ? section.getPgMar()
                : section.addNewPgMar();

        margins.setTop(BigInteger.valueOf(850));
        margins.setBottom(BigInteger.valueOf(850));
        margins.setLeft(BigInteger.valueOf(1050));
        margins.setRight(BigInteger.valueOf(1050));
    }

    private void addHeading(
            XWPFDocument document,
            String text
    ) {
        XWPFParagraph paragraph = document.createParagraph();

        formatParagraph(paragraph, 12, 5);
        keepWithNext(paragraph);

        addText(paragraph, text, 20, true);

        CTPPr properties = paragraph.getCTP().getPPr();

        if (properties == null) {
            properties = paragraph.getCTP().addNewPPr();
        }

        CTPBdr borders = properties.isSetPBdr()
                ? properties.getPBdr()
                : properties.addNewPBdr();

        CTBorder bottom = borders.isSetBottom()
                ? borders.getBottom()
                : borders.addNewBottom();

        bottom.setVal(STBorder.SINGLE);
        bottom.setSz(BigInteger.valueOf(6));
        bottom.setColor("333333");
        bottom.setSpace(BigInteger.valueOf(3));
    }

    private void addEntry(
            XWPFDocument document,
            String title,
            String date,
            String subtitle
    ) {
        XWPFParagraph paragraph = document.createParagraph();

        formatParagraph(paragraph, 5, 2);
        keepWithNext(paragraph);

        if (date != null && !date.isBlank()) {
            CTPPr properties = paragraph.getCTP().getPPr();

            if (properties == null) {
                properties = paragraph.getCTP().addNewPPr();
            }

            CTTabs tabs = properties.isSetTabs()
                    ? properties.getTabs()
                    : properties.addNewTabs();

            CTTabStop tabStop = tabs.addNewTab();

            tabStop.setVal(STTabJc.RIGHT);
            tabStop.setPos(BigInteger.valueOf(9800));
        }

        addText(
                paragraph,
                title,
                BODY_FONT_SIZE,
                true
        );

        if (date != null && !date.isBlank()) {
            XWPFRun dateRun = paragraph.createRun();
            dateRun.addTab();
            dateRun.setFontFamily("Arial");
            dateRun.setFontSize(10.5);
            dateRun.setText(date);
        }

        if (subtitle != null && !subtitle.isBlank()) {
            XWPFParagraph sub = document.createParagraph();

            formatParagraph(sub, 0, 3);

            addText(
                    sub,
                    subtitle,
                    BODY_FONT_SIZE,
                    false
            );
        }
    }

    private void addBullets(
            XWPFDocument document,
            List<String> bullets
    ) {
        if (bullets == null) {
            return;
        }

        for (String bullet : bullets) {
            if (bullet == null || bullet.isBlank()) {
                continue;
            }

            XWPFParagraph paragraph = document.createParagraph();

            formatParagraph(paragraph, 0, 2);
            paragraph.setIndentationLeft(300);
            paragraph.setIndentationHanging(180);

            addText(
                    paragraph,
                    "•  " + bullet,
                    BODY_FONT_SIZE,
                    false
            );
        }
    }

    private void keepWithNext(XWPFParagraph paragraph) {
        CTPPr properties = paragraph.getCTP().getPPr();

        if (properties == null) {
            properties = paragraph.getCTP().addNewPPr();
        }

        if (!properties.isSetKeepNext()) {
            properties.addNewKeepNext();
        }
    }

    private void formatParagraph(
            XWPFParagraph paragraph,
            int before,
            int after
    ) {
        paragraph.setSpacingBefore(before * 20);
        paragraph.setSpacingAfter(after * 20);
        paragraph.setSpacingBetween(1.08);
    }

    private void addText(
            XWPFParagraph paragraph,
            String text,
            int halfPoints,
            boolean bold
    ) {
        XWPFRun run = paragraph.createRun();

        run.setFontFamily("Arial");
        run.setFontSize(halfPoints / 2.0);
        run.setBold(bold);
        run.setText(Objects.toString(text, ""));
    }

    private boolean hasItems(List<?> items) {
        return items != null && !items.isEmpty();
    }

    private String joinNonBlank(String... values) {
        return Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(" | "));
    }
}
