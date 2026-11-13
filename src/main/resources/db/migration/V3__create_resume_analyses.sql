
CREATE TABLE resume_analyses (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL UNIQUE,
    profile_json TEXT,
    CONSTRAINT fk_resume_analysis_resume
        FOREIGN KEY (resume_id)
        REFERENCES resumes(id)
        ON DELETE CASCADE
);
