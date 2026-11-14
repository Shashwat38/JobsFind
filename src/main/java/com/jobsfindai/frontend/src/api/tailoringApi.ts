
export type TailoredEducation = {
    degree: string;
    institution: string;
    graduationDate: string;
};

export type TailoredExperience = {
    title: string;
    company: string;
    dates: string;
    bullets: string[];
};

export type TailoredProject = {
    name: string;
    bullets: string[];
};

export type TailoredResume = {
    name: string;
    email: string;
    phone: string;
    location: string;
    education: TailoredEducation[];
    workExperience: TailoredExperience[];
    projects: TailoredProject[];
    technicalSkills: string[];
};

export async function tailorResume(
    token: string,
    resumeId: number,
    jobId: string
): Promise<TailoredResume> {
    const response = await fetch(
        `http://localhost:8080/api/resumes/${resumeId}/tailor`,
        {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ jobId })
        }
    );

    if (!response.ok) {
        throw new Error('Failed to tailor CV');
    }

    return response.json();
}
