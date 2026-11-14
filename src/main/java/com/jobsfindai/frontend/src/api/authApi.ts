
export async function login(email: string, password: string) {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    });

    if (!response.ok) {
        throw new Error('Login failed');
    }

    return response.json();
}

export async function register(
    email: string,
    password: string
) {
    const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
        throw new Error('Registration failed');
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}