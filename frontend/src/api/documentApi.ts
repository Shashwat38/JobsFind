
import type { TailoredResume } from "./tailoringApi";

export type DocumentFormat = "word" | "pdf";

export async function downloadTailoredResume(
    token: string,
    resume: TailoredResume,
    format: DocumentFormat
): Promise<void> {

    const response = await fetch(
        `/api/documents/${format}`,
        {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(resume)
        }
    );

    if (!response.ok) {
        throw new Error("Failed to download tailored CV");
    }

    const blob = await response.blob();

    const url = window.URL.createObjectURL(blob);

    const link = document.createElement("a");

    link.href = url;

    link.download =
        format === "word"
            ? "tailored-resume.docx"
            : "tailored-resume.pdf";

    document.body.appendChild(link);

    link.click();

    document.body.removeChild(link);

    window.URL.revokeObjectURL(url);
}
