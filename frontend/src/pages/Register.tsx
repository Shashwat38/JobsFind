
import { useState } from 'react';
import { register } from '../api/authApi';

function Register() {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    async function handleRegister(event: React.SubmitEvent<HTMLFormElement>) {
        event.preventDefault();

        try {
            await register(email, password);
            setMessage('Registration successful! You can now log in.');
        } catch {
            setMessage('Registration failed.');
        }
    }

    return (
        <div>
            <h2>Create Account</h2>

            <form onSubmit={handleRegister}>
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
                    minLength={8}
                    required
                />

                <button type="submit">
                    Register
                </button>
            </form>

            {message && <p role="status">{message}</p>}
        </div>
    );
}

export default Register;
