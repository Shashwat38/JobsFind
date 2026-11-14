
export interface JobResult {
    id: string;
    title: string;
    employer: string;
    location: string;
    description: string;
    applicationUrl: string;
    matchScore: number;
}

const API_URL = "/api/jobs";

async function readJobsResponse(response: Response): Promise<JobResult[]> {
    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }

    const text = await response.text();

    if (!text.trim()) {
        return [];
    }

    return JSON.parse(text) as JobResult[];
}

export async function getJobRecommendations(
    token: string,
    resumeId: number,
    location: string
): Promise<JobResult[]> {
    const params = new URLSearchParams({ location });

    const response = await fetch(
        `${API_URL}/recommendations/${resumeId}?${params}`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return readJobsResponse(response);
}

export async function getSavedJobRecommendations(
    token: string,
    resumeId: number,
    location: string
): Promise<JobResult[]> {
    const params = new URLSearchParams({ location });

    const response = await fetch(
        `${API_URL}/recommendations/${resumeId}/saved?${params}`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return readJobsResponse(response);
}

export async function refreshJobRecommendations(
    token: string,
    resumeId: number,
    location: string
): Promise<JobResult[]> {
    const params = new URLSearchParams({ location });

    const response = await fetch(
        `${API_URL}/recommendations/${resumeId}/refresh?${params}`,
        {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return readJobsResponse(response);
}
