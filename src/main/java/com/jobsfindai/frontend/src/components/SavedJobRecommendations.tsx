
import { useEffect, useRef, useState } from 'react';
import {
    getSavedJobRecommendations,
    type JobResult
} from '../api/jobApi';
import {
    tailorResume,
    type TailoredResume
} from '../api/tailoringApi';

import LocationSelector from './LocationSelector';
import TailoredResumePreview from './TailoredResumePreview';

type SavedJobRecommendationsProps = {
    token: string;
    resumeId: number;
};

export default function SavedJobRecommendations({
                                                    token,
                                                    resumeId
                                                }: SavedJobRecommendationsProps) {

    const [location, setLocation] = useState('Stockholm');
    const [jobs, setJobs] = useState<JobResult[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [tailoringJobId, setTailoringJobId] = useState<string | null>(null);
    const [tailoredResume, setTailoredResume] = useState<TailoredResume | null>(null);
    const [previewJobId, setPreviewJobId] = useState<string | null>(null);
    const [tailoringError, setTailoringError] = useState('');

    const tailoringRequestId = useRef(0);
    const tailoringInProgress = useRef(false);

    useEffect(() => {
        let active = true;

        setJobs([]);
        setLoading(true);
        setError('');

        async function loadSavedJobs() {
            try {
                const results = await getSavedJobRecommendations(
                    token,
                    resumeId,
                    location
                );

                if (active) {
                    setJobs(results ?? []);
                }
            } catch {
                if (active) {
                    setError('Could not load saved recommendations.');
                }
            } finally {
                if (active) {
                    setLoading(false);
                }
            }
        }

        void loadSavedJobs();

        return () => {
            active = false;
        };
    }, [token, resumeId, location]);

    useEffect(() => {
        tailoringRequestId.current++;
        tailoringInProgress.current = false;
        setTailoringJobId(null);
        setTailoredResume(null);
        setPreviewJobId(null);
        setTailoringError('');
    }, [token, resumeId, location]);

    async function handleTailor(jobId: string) {
        if (tailoringInProgress.current || loading) {
            return;
        }

        const currentRequest = ++tailoringRequestId.current;

        tailoringInProgress.current = true;
        setTailoringJobId(jobId);
        setTailoredResume(null);
        setPreviewJobId(null);
        setTailoringError('');

        try {
            const result = await tailorResume(
                token,
                resumeId,
                jobId
            );

            if (currentRequest !== tailoringRequestId.current) {
                return;
            }

            setTailoredResume(result);
            setPreviewJobId(jobId);
        } catch {
            if (currentRequest === tailoringRequestId.current) {
                setTailoringError(
                    'Could not tailor CV. Please try again.'
                );
            }
        } finally {
            if (currentRequest === tailoringRequestId.current) {
                tailoringInProgress.current = false;
                setTailoringJobId(null);
            }
        }
    }

    function handleClosePreview() {
        setTailoredResume(null);
        setPreviewJobId(null);
    }

    return (
        <section className="panel">
            <h2>Saved Job Recommendations</h2>

            <div className="job-search-form">
                <LocationSelector
                    token={token}
                    value={location}
                    onChange={setLocation}
                />
            </div>

            {loading && <p>Loading saved recommendations...</p>}

            {error && <p role="alert">{error}</p>}

            {!loading && !error && jobs.length === 0 && (
                <p>
                    No saved recommendations for this CV and location.
                    Generate recommendations in the Find Jobs page first.
                </p>
            )}

            {!loading && !error && jobs.length > 0 && (
                <div className="job-results">
                    <h3>Saved Jobs ({jobs.length})</h3>

                    {jobs.map((job) => (
                        <article className="job-card" key={job.id}>
                            <div className="job-card-header">
                                <div>
                                    <h3>{job.title}</h3>
                                    <p>
                                        {job.employer} · {job.location}
                                    </p>
                                </div>

                                <span className="match-score">
                                    {job.matchScore}% match
                                </span>
                            </div>

                            <p className="job-description">
                                {job.description
                                    ? job.description.slice(0, 250) + '...'
                                    : 'No description available.'}
                            </p>

                            {job.applicationUrl && (
                                <a
                                    href={job.applicationUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="job-link"
                                >
                                    View Job
                                </a>
                            )}

                            <button
                                type="button"
                                onClick={() => handleTailor(job.id)}
                                disabled={
                                    loading ||
                                    tailoringJobId !== null
                                }
                            >
                                {tailoringJobId === job.id
                                    ? 'Tailoring CV...'
                                    : 'Tailor CV'}
                            </button>

                            {tailoredResume && previewJobId === job.id && (
                                <div className="tailored-preview-container">
                                    <div className="tailored-preview-toolbar">
                                        <h3>Tailored CV</h3>

                                        <button
                                            type="button"
                                            onClick={handleClosePreview}
                                        >
                                            Close Preview
                                        </button>
                                    </div>

                                    <TailoredResumePreview
                                        resume={tailoredResume}
                                    />
                                </div>
                            )}
                        </article>
                    ))}
                </div>
            )}

            {tailoringError && (
                <p role="alert">{tailoringError}</p>
            )}
        </section>
    );
}
