
const API_URL = 'http://localhost:8080/api/municipalities';

let cachedMunicipalities: string[] | null = null;

export async function getMunicipalities(
    token: string
): Promise<string[]> {

    if (cachedMunicipalities !== null) {
        return cachedMunicipalities;
    }

    const response = await fetch(API_URL, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Could not load municipalities');
    }

    const data = (await response.json()) as string[];

    cachedMunicipalities = data;
    return data;
}
