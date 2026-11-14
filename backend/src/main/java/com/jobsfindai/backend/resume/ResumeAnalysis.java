package com.jobsfindai.backend.resume;

import com.jobsfindai.backend.resume.Resume;
import jakarta.persistence.*;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false, unique = true)
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String profileJson;

    protected ResumeAnalysis() {}

    public ResumeAnalysis(Resume resume, String profileJson) {
        this.resume = resume;
        this.profileJson = profileJson;
    }

    public Long getId() {
        return id;
    }

    public Resume getResume() {
        return resume;
    }

    public String getProfileJson() {
        return profileJson;
    }
}
