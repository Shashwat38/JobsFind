
import { useEffect, useState } from 'react';
import ResumeUpload from '../components/ResumeUpload';
import JobRecommendations from '../components/JobRecommendations';
import SavedJobRecommendations from '../components/SavedJobRecommendations';

import {
    getResumes,
    getResumeAnalysis,
    downloadResume,
    deleteResume
} from '../api/resumeApi';

type Resume = {
    id: number;
    filename: string;
};

type ResumeProfile = {
    name: string;
    skills: string[];
    education: string;
    experience: string[];
    summary: string;
};

type DashboardProps = {
    token: string;
    onLogout: () => void;
};

type Tab = 'dashboard' | 'resumes' | 'jobs';

export default function Dashboard({
                                      token,
                                      onLogout
                                  }: DashboardProps) {

    const [activeTab, setActiveTab] = useState<Tab>('dashboard');
    const [resumes, setResumes] = useState<Resume[]>([]);
    const [selectedAnalysis, setSelectedAnalysis] =
        useState<ResumeProfile | null>(null);
    const [selectedJobsResumeId, setSelectedJobsResumeId] =
        useState<number | null>(null);
    const [analysisLoading, setAnalysisLoading] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const modalOpen =
        selectedAnalysis !== null ||
        selectedJobsResumeId !== null ||
        analysisLoading;

    function closeModal() {
        setSelectedAnalysis(null);
        setSelectedJobsResumeId(null);
        setAnalysisLoading(false);
    }

    useEffect(() => {
        void loadResumes();
    }, [token]);

    useEffect(() => {
        if (!modalOpen) {
            return;
        }

        function handleKeyDown(event: KeyboardEvent) {
            if (event.key === 'Escape') {
                closeModal();
            }
        }

        window.addEventListener('keydown', handleKeyDown);

        const previousOverflow = document.body.style.overflow;
        document.body.style.overflow = 'hidden';

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
            document.body.style.overflow = previousOverflow;
        };
    }, [modalOpen]);

    async function loadResumes() {
        try {
            setLoading(true);
            setError('');

            const data = await getResumes(token);
            setResumes(data);
        } catch {
            setError('Could not load your resumes.');
        } finally {
            setLoading(false);
        }
    }

    async function handleUploadSuccess() {
        await loadResumes();
        closeModal();
    }

    async function handleViewAnalysis(resumeId: number) {
        try {
            setError('');
            setSelectedJobsResumeId(null);
            setSelectedAnalysis(null);
            setAnalysisLoading(true);

            const analysis = await getResumeAnalysis(token, resumeId);

            setSelectedAnalysis(analysis);
        } catch {
            setError('Could not load CV analysis.');
        } finally {
            setAnalysisLoading(false);
        }
    }

    function handleViewJobs(resumeId: number) {
        setError('');
        setSelectedAnalysis(null);
        setAnalysisLoading(false);
        setSelectedJobsResumeId(resumeId);
    }

    async function handleDownload(resume: Resume) {
        try {
            setError('');

            const blob = await downloadResume(token, resume.id);

            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');

            link.href = url;
            link.download = resume.filename;

            document.body.appendChild(link);
            link.click();
            link.remove();

            URL.revokeObjectURL(url);
        } catch {
            setError('Could not download the resume.');
        }
    }

    async function handleDelete(resumeId: number) {
        const confirmed = window.confirm(
            'Are you sure you want to delete this resume?'
        );

        if (!confirmed) {
            return;
        }

        try {
            setError('');

            await deleteResume(token, resumeId);
            await loadResumes();
            closeModal();
        } catch {
            setError('Could not delete the resume.');
        }
    }

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <h1>JobsFind</h1>

                <button onClick={onLogout}>
                    Logout
                </button>
            </header>

            <nav className="dashboard-nav">
                <button
                    className={activeTab === 'dashboard' ? 'active' : ''}
                    onClick={() => setActiveTab('dashboard')}
                >
                    Dashboard
                </button>

                <button
                    className={activeTab === 'resumes' ? 'active' : ''}
                    onClick={() => setActiveTab('resumes')}
                >
                    My Resumes
                </button>

                <button
                    className={activeTab === 'jobs' ? 'active' : ''}
                    onClick={() => setActiveTab('jobs')}
                >
                    Find Jobs
                </button>
            </nav>

            {error && <p role="alert" style={{ color: 'red' }}>{error}</p>}

            {activeTab === 'dashboard' && (
                <section className="panel">
                    <h2>Welcome to JobsFind</h2>

                    <p>
                        Upload your CV, manage your resumes
                        and discover jobs matched to your skills.
                    </p>

                    <div className="dashboard-overview">
                        <div className="overview-card">
                            <h3>My Resumes</h3>
                            <p>
                                You have {resumes.length} uploaded CV(s).
                            </p>
                            <button onClick={() => setActiveTab('resumes')}>
                                Manage Resumes
                            </button>
                        </div>

                        <div className="overview-card">
                            <h3>Find Jobs</h3>
                            <p>
                                Discover relevant job opportunities
                                based on your CV.
                            </p>
                            <button onClick={() => setActiveTab('jobs')}>
                                Find Matching Jobs
                            </button>
                        </div>
                    </div>
                </section>
            )}

            {activeTab === 'resumes' && (
                <>
                    <section className="panel">
                        <h2>Upload Resume</h2>
                        <ResumeUpload
                            token={token}
                            onUploadSuccess={handleUploadSuccess}
                        />
                    </section>

                    <section className="panel">
                        <h2>My Resumes</h2>

                        {loading && <p>Loading resumes...</p>}

                        {!loading && resumes.length === 0 && (
                            <p>You have not uploaded any resumes yet.</p>
                        )}

                        <div className="resume-list">
                            {resumes.map((resume) => (
                                <div
                                    key={resume.id}
                                    className="resume-card"
                                >
                                    <h3>{resume.filename}</h3>

                                    <div className="resume-actions">
                                        <button
                                            onClick={() =>
                                                void handleViewAnalysis(resume.id)
                                            }
                                        >
                                            View Analysis
                                        </button>

                                        <button
                                            onClick={() =>
                                                handleViewJobs(resume.id)
                                            }
                                        >
                                            View Recommended Jobs
                                        </button>

                                        <button
                                            onClick={() =>
                                                void handleDownload(resume)
                                            }
                                        >
                                            Download
                                        </button>

                                        <button
                                            onClick={() =>
                                                void handleDelete(resume.id)
                                            }
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>
                </>
            )}

            <div
                style={{
                    display: activeTab === 'jobs' ? 'block' : 'none'
                }}
            >
                <JobRecommendations
                    token={token}
                    resumes={resumes}
                />
            </div>

            {modalOpen && (
                <div
                    className="modal-overlay"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            closeModal();
                        }
                    }}
                >
                    <div
                        className="modal-content"
                        role="dialog"
                        aria-modal="true"
                        aria-label={
                            selectedJobsResumeId !== null
                                ? 'Saved Job Recommendations'
                                : 'CV Analysis'
                        }
                    >
                        <button
                            className="modal-close"
                            onClick={closeModal}
                            aria-label="Close popup"
                        >
                            ×
                        </button>

                        {analysisLoading && (
                            <p>Loading CV analysis...</p>
                        )}

                        {selectedAnalysis && (
                            <div className="modal-analysis">
                                <h2>CV Analysis</h2>

                                <h3>Name</h3>
                                <p>{selectedAnalysis.name}</p>

                                <h3>Summary</h3>
                                <p>{selectedAnalysis.summary}</p>

                                <h3>Education</h3>
                                <p>{selectedAnalysis.education}</p>

                                <h3>Skills</h3>
                                <ul>
                                    {selectedAnalysis.skills.map(
                                        (skill, index) => (
                                            <li key={index}>{skill}</li>
                                        )
                                    )}
                                </ul>

                                <h3>Experience</h3>
                                <ul>
                                    {selectedAnalysis.experience.map(
                                        (experience, index) => (
                                            <li key={index}>{experience}</li>
                                        )
                                    )}
                                </ul>
                            </div>
                        )}

                        {selectedJobsResumeId !== null && (
                            <SavedJobRecommendations
                                token={token}
                                resumeId={selectedJobsResumeId}
                            />
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
