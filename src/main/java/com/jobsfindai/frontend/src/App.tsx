
import { useState } from 'react';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import './App.css';

function App() {

    const [token, setToken] = useState<string | null>(() => {
        return localStorage.getItem('token');
    });

    const [showRegister, setShowRegister] = useState(false);

    function handleLogin(newToken: string) {
        localStorage.setItem('token', newToken);
        setToken(newToken);
    }

    function handleLogout() {
        localStorage.removeItem('token');
        setToken(null);
    }

    if (token) {
        return (
            <Dashboard
                token={token}
                onLogout={handleLogout}
            />
        );
    }

    return (
        <div>
            <h1>JobsFindAI</h1>

            {showRegister ? (
                <Register />
            ) : (
                <Login onLogin={handleLogin} />
            )}

            <button
                onClick={() => setShowRegister(!showRegister)}
            >
                {showRegister
                    ? 'Already have an account? Login'
                    : 'Create an account'}
            </button>
        </div>
    );
}

export default App;
