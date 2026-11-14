
export async function getResumes(token: string) {
    const response = await fetch('/api/resumes', {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch resumes');
    }

    return response.json();
}


export async function uploadResume(token: string, file: File) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('/api/resume/upload', {
        method: 'POST',
        headers: {
            Authorization: `Bearer ${token}`
        },
        body: formData
    });

    if (!response.ok) {
        throw new Error('Failed to upload resume');
    }

    return response.json();
}

export async function deleteResume(token: string, resumeId: number) {
    const response = await fetch(`/api/resumes/${resumeId}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to delete resume');
    }
}

export async function downloadResume(token: string, resumeId: number) {
    const response = await fetch(`/api/resumes/${resumeId}/download`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to download resume');
    }

    return response.blob();
}

export async function getResumeAnalysis(token: string, resumeId: number) {
    const response = await fetch(`/api/resumes/${resumeId}/analysis`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch resume analysis');
    }

    return response.json();
}

