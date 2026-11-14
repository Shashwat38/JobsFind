
import type { TailoredResume } from '../api/tailoringApi';

type TailoredResumePreviewProps = {
    resume: TailoredResume;
};

export default function TailoredResumePreview({
                                                  resume
                                              }: TailoredResumePreviewProps) {

    return (
        <div className="tailored-resume-preview">
            <header className="tailored-resume-header">
                <h1>{resume.name}</h1>

                <p>
                    {[
                        resume.location,
                        resume.email,
                        resume.phone
                    ].filter(Boolean).join(' | ')}
                </p>
            </header>

            {resume.education?.length > 0 && (
                <section className="tailored-resume-section">
                    <h2>Education</h2>

                    {resume.education.map((education, index) => (
                        <div key={index}>
                            <div className="tailored-resume-entry-header">
                                <strong>{education.degree}</strong>
                                <span>{education.graduationDate}</span>
                            </div>

                            <p>{education.institution}</p>
                        </div>
                    ))}
                </section>
            )}

            {resume.workExperience?.length > 0 && (
                <section className="tailored-resume-section">
                    <h2>Work Experience</h2>

                    {resume.workExperience.map((experience, index) => (
                        <div key={index}>
                            <div className="tailored-resume-entry-header">
                                <strong>{experience.title}</strong>
                                <span>{experience.dates}</span>
                            </div>

                            <p>{experience.company}</p>

                            <ul>
                                {experience.bullets?.map((bullet, bulletIndex) => (
                                    <li key={bulletIndex}>{bullet}</li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </section>
            )}

            {resume.projects?.length > 0 && (
                <section className="tailored-resume-section">
                    <h2>Projects</h2>

                    {resume.projects.map((project, index) => (
                        <div key={index}>
                            <strong>{project.name}</strong>

                            <ul>
                                {project.bullets?.map((bullet, bulletIndex) => (
                                    <li key={bulletIndex}>{bullet}</li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </section>
            )}

            {resume.technicalSkills?.length > 0 && (
                <section className="tailored-resume-section">
                    <h2>Technical Skills</h2>

                    <p>{resume.technicalSkills.join(' | ')}</p>
                </section>
            )}
        </div>
    );
}
