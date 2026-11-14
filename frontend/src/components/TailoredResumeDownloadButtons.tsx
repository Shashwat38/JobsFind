
import { useState } from "react";
import {
    downloadTailoredResume,
    type DocumentFormat
} from "../api/documentApi";
import type { TailoredResume } from "../api/tailoringApi";

type Props = {
    token: string;
    resume: TailoredResume;
};

export default function TailoredResumeDownloadButtons({
                                                          token,
                                                          resume
                                                      }: Props) {

    const [downloading, setDownloading] =
        useState<DocumentFormat | null>(null);

    const [error, setError] = useState<string | null>(null);

    async function handleDownload(format: DocumentFormat) {
        setDownloading(format);
        setError(null);

        try {
            await downloadTailoredResume(token, resume, format);
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Download failed"
            );
        } finally {
            setDownloading(null);
        }
    }

    return (
        <div className="tailored-resume-downloads">
            <button
                type="button"
                disabled={downloading !== null}
                onClick={() => handleDownload("word")}
            >
                {downloading === "word"
                    ? "Downloading..."
                    : "Download Word"}
            </button>

            <button
                type="button"
                disabled={downloading !== null}
                onClick={() => handleDownload("pdf")}
            >
                {downloading === "pdf"
                    ? "Downloading..."
                    : "Download PDF"}
            </button>

            {error && (
                <p role="alert" className="download-error">
                    {error}
                </p>
            )}
        </div>
    );
}
