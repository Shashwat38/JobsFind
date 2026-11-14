
import { useState } from 'react';
import { uploadResume } from '../api/resumeApi';

type ResumeUploadProps = {
    token: string;
    onUploadSuccess: () => void;
};

function ResumeUpload({ token, onUploadSuccess }: ResumeUploadProps) {
    const [file, setFile] = useState<File | null>(null);
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState('');

    async function handleUpload(event: React.SubmitEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!file || uploading) {
            return;
        }

        setUploading(true);
        setError('');

        try {
            await uploadResume(token, file);
            setFile(null);
            onUploadSuccess();
        } catch {
            setError('Could not upload CV.');
        } finally {
            setUploading(false);
        }
    }

    return (
        <section className="panel">
            <h2>Upload CV</h2>

            <form onSubmit={handleUpload}>
                <input
                    type="file"
                    accept=".pdf,application/pdf"
                    onChange={(event) => {
                        setFile(event.target.files?.[0] ?? null);
                    }}
                    required
                />

                <button type="submit" disabled={!file || uploading}>
                    {uploading ? 'Analyzing CV...' : 'Upload CV'}
                </button>
            </form>

            {error && <p role="alert">{error}</p>}
        </section>
    );
}

export default ResumeUpload;
