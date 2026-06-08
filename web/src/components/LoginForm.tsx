import React, { useState } from 'react';
import { useAuth } from '../Context/AuthContext';
import { useNavigate } from 'react-router-dom';

export const LoginForm: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const { loginUser, user } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!email || !password) {
            setError('Veuillez remplir tous les champs.');
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            await loginUser({ email: email.toLowerCase().trim(), password });
            navigate('/dashboard');

        } catch (err: any) {
            setError(err.message || "Identifiants incorrects ou problème réseau.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form className="flex flex-col gap-4 max-w-md mx-auto p-4" onSubmit={handleSubmit}>
            <h2 className="text-brand-amber text-center font-bold text-xl uppercase mb-2">
                CONNEXION
            </h2>


            {error && (
                <div className="text-red-500 text-sm text-center bg-red-500/10 py-2 rounded-lg">
                    {error}
                </div>
            )}

            {/* Email */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">📧 EMAIL</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    className="w-full border border-white/10 rounded-lg px-4 py-2 text-white bg-transparent outline-none focus:border-brand-amber transition-all"
                />
            </div>

            {/* Mot de passe */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">🔒 MOT DE PASSE</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    className="w-full border border-white/10 rounded-lg px-4 py-2 text-white bg-transparent outline-none focus:border-brand-amber transition-all"
                />
            </div>

            {/* Bouton de validation */}
            <button
                type="submit"
                disabled={isLoading}
                className="mt-4 bg-brand-amber hover:bg-amber-500 disabled:bg-gray-600 text-brand-dark font-bold py-3 rounded-lg uppercase tracking-tighter transition-transform active:scale-95"
            >
                {isLoading ? 'Connexion en cours...' : 'SE CONNECTER'}
            </button>
        </form>
    );
};

export default LoginForm;