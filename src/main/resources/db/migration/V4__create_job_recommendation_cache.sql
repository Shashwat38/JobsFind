CREATE TABLE job_recommendation_cache(
    id BIGSERIAL PRIMARY KEY,

    resume_id BIGINT NOT NULL
     REFERENCES resumes(id)
         ON DELETE CASCADE,

    location VARCHAR(100) NOT NULL,

    jobs_json TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_recommendation_resume_location
     UNIQUE (resume_id, location)

);