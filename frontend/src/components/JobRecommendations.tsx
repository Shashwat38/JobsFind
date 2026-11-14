
import { useEffect, useRef, useState } from 'react';
import {
    getJobRecommendations,
    getSavedJobRecommendations,
    refreshJobRecommendations,
    type JobResult
} from '../api/jobApi';
import {
    tailorResume,
    type TailoredResume
} from '../api/tailoringApi';

import TailoredResumeDownloadButtons from
        "../components/TailoredResumeDownloadButtons";

import LocationSelector from './LocationSelector';
import TailoredResumePreview from './TailoredResumePreview';

type Resume = {
    id: number;
    filename: string;
};

type JobRecommendationsProps = {
    token: string;
    resumes: Resume[];
};

export default function JobRecommendations({
                                               token,
                                               resumes
                                           }: JobRecommendationsProps) {

    const [selectedResumeId, setSelectedResumeId] = useState('');
    const [location, setLocation] = useState('Stockholm');
    const [jobs, setJobs] = useState<JobResult[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [searched, setSearched] = useState(false);
    const [hasSavedJobs, setHasSavedJobs] = useState(false);

    const [tailoringJobId, setTailoringJobId] = useState<string | null>(null);
    const [tailoredResume, setTailoredResume] = useState<TailoredResume | null>(null);
    const [previewJobId, setPreviewJobId] = useState<string | null>(null);
    const [tailoringError, setTailoringError] = useState('');

    const requestId = useRef(0);
    const tailoringRequestId = useRef(0);
    const tailoringInProgress = useRef(false);

    useEffect(() => {
        if (
            selectedResumeId &&
            !resumes.some(
                (resume) => String(resume.id) === selectedResumeId
            )
        ) {
            setSelectedResumeId('');
        }
    }, [resumes, selectedResumeId]);

    useEffect(() => {
        const currentRequest = ++requestId.current;
        let active = true;

        setJobs([]);
        setError('');
        setSearched(false);
        setHasSavedJobs(false);

        if (!selectedResumeId) {
            setLoading(false);
            return;
        }

        setLoading(true);

        async function loadSavedJobs() {
            try {
                const results = await getSavedJobRecommendations(
                    token,
                    Number(selectedResumeId),
                    location
                );

                if (!active || currentRequest !== requestId.current) {
                    return;
                }

                setJobs(results ?? []);
                setHasSavedJobs((results ?? []).length > 0);
                setSearched((results ?? []).length > 0);
            } catch {
                if (active && currentRequest === requestId.current) {
                    setError('Could not load saved recommendations.');
                }
            } finally {
                if (active && currentRequest === requestId.current) {
                    setLoading(false);
                }
            }
        }

        void loadSavedJobs();

        return () => {
            active = false;
            requestId.current++;
        };
    }, [token, selectedResumeId, location]);

    useEffect(() => {
        tailoringRequestId.current++;
        tailoringInProgress.current = false;
        setTailoringJobId(null);
        setTailoredResume(null);
        setPreviewJobId(null);
        setTailoringError('');
    }, [token, selectedResumeId, location]);

    async function handleSearch() {
        if (!selectedResumeId || loading) {
            return;
        }

        const currentRequest = ++requestId.current;

        setLoading(true);
        setError('');

        try {
            const results = await getJobRecommendations(
                token,
                Number(selectedResumeId),
                location
            );

            if (currentRequest !== requestId.current) {
                return;
            }

            setJobs(results ?? []);
            setSearched(true);
            setHasSavedJobs(true);
            setTailoredResume(null);
            setPreviewJobId(null);
        } catch {
            if (currentRequest === requestId.current) {
                setError('Could not find job recommendations.');
            }
        } finally {
            if (currentRequest === requestId.current) {
                setLoading(false);
            }
        }
    }

    async function handleRefresh() {
        if (!selectedResumeId || loading) {
            return;
        }

        const currentRequest = ++requestId.current;

        setLoading(true);
        setError('');

        try {
            const results = await refreshJobRecommendations(
                token,
                Number(selectedResumeId),
                location
            );

            if (currentRequest !== requestId.current) {
                return;
            }

            setJobs(results ?? []);
            setSearched(true);
            setHasSavedJobs(true);
            setTailoredResume(null);
            setPreviewJobId(null);
        } catch {
            if (currentRequest === requestId.current) {
                setError('Could not refresh job recommendations.');
            }
        } finally {
            if (currentRequest === requestId.current) {
                setLoading(false);
            }
        }
    }

    async function handleTailor(jobId: string) {
        if (
            !selectedResumeId ||
            loading ||
            tailoringInProgress.current
        ) {
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
                Number(selectedResumeId),
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
            <h2>Find Jobs</h2>

            <div className="job-search-form">
                <label htmlFor="resume-select">Choose CV</label>

                <select
                    id="resume-select"
                    value={selectedResumeId}
                    onChange={(event) =>
                        setSelectedResumeId(event.target.value)
                    }
                >
                    <option value="">Select a CV</option>

                    {resumes.map((resume) => (
                        <option key={resume.id} value={resume.id}>
                            {resume.filename}
                        </option>
                    ))}
                </select>

                <LocationSelector
                    token={token}
                    value={location}
                    onChange={setLocation}
                />

                <button
                    onClick={hasSavedJobs ? handleRefresh : handleSearch}
                    disabled={!selectedResumeId || loading}
                >
                    {loading
                        ? 'Loading recommendations...'
                        : hasSavedJobs
                            ? 'Refresh Recommendations'
                            : 'Find Matching Jobs'}
                </button>
            </div>

            {error && <p role="alert">{error}</p>}

            {searched && jobs.length === 0 && (
                <p>No matching jobs found.</p>
            )}

            {jobs.length > 0 && (
                <div className="job-results">
                    <h3>Recommended Jobs ({jobs.length})</h3>

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
                                    !selectedResumeId ||
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

                                        <div className="tailored-preview-actions">
                                            <TailoredResumeDownloadButtons
                                                token={token}
                                                resume={tailoredResume}
                                            />

                                            <button
                                                type="button"
                                                onClick={handleClosePreview}
                                            >
                                                Close Preview
                                            </button>
                                        </div>
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
