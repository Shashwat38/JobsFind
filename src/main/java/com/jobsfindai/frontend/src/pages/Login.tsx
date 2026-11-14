
import { useState } from 'react';
import { login } from '../api/authApi';

type LoginProps = {
    onLogin: (token: string) => void;
};

function Login({ onLogin }: LoginProps) {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    async function handleLogin(event: React.SubmitEvent<HTMLFormElement>) {
        event.preventDefault();

        try {
            const data = await login(email, password);

            onLogin(data.token);

        } catch {
            setMessage('Invalid email or password');
        }
    }

    return (
        <div>
            <h2>Login</h2>

            <form onSubmit={handleLogin}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    required
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    required
                />

                <button type="submit">
                    Login
                </button>
            </form>

            <p>{message}</p>
        </div>
    );
}

export default Login;
