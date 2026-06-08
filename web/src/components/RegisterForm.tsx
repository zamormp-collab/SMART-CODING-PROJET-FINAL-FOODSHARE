import React, { useState, ChangeEvent, FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { RegisterRequest } from '../types';
import { useAuth } from '../Context/AuthContext';

const RegisterForm: React.FC = () => {
    const navigate = useNavigate();
    const { loginUser } = useAuth();

    const [formData, setFormData] = useState<RegisterRequest>({
        nom: '',
        prenom: '',
        email: '',
        motDePasse: '',
        role: 'OFFREUR',
        adresse: '',
        telephone: '',
    });

    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);

        if (formData.motDePasse.trim() !== confirmPassword.trim()) {
            setError("Les mots de passe ne correspondent pas.");
            return;
        }

        setIsLoading(true);

        try {
            await authService.register(formData);

            await loginUser({
                email: formData.email.toLowerCase().trim(),
                password: formData.motDePasse,
            });

            navigate('/dashboard');

        } catch (err: any) {
            setError(err.message || "Erreur lors de l'inscription. L'adresse e-mail est peut-être déjà utilisée.");
        } finally {
            setIsLoading(false);
        }
    };

    const inputClass = "w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all bg-transparent";
    //Message de Bienvenue personnaliser
    const prenomSaisi = formData.prenom.trim();

    return (
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">

                <h2 className="text-brand-amber text-center font-bold text-xl uppercase mb-2">
                    Inscription 
                </h2>

            {/* Nom */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">👤Nom</label>
                <input
                    type="text"
                    name="nom"
                    required
                    value={formData.nom}
                    onChange={handleChange}
                    className={inputClass}
                />
            </div>

            {/* Prénom */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">👤Prénom</label>
                <input
                    type="text"
                    name="prenom"
                    required
                    value={formData.prenom}
                    onChange={handleChange}
                    className={inputClass}
                />
            </div>

            {/* Email */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">📧 Email</label>
                <input
                    type="email"
                    name="email"
                    required
                    value={formData.email}
                    onChange={handleChange}
                    className={inputClass}
                />
            </div>

            {/* Téléphone */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">📱Téléphone</label>
                <input
                    type="tel"
                    name="telephone"
                    value={formData.telephone}
                    onChange={handleChange}
                    className={inputClass}
                />
            </div>

            {/* Adresse */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">📍Adresse</label>
                <input
                    type="text"
                    name="adresse"
                    value={formData.adresse}
                    onChange={handleChange}
                    className={inputClass}
                />
            </div>

            {/* Mot de passe */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">🔒 Mot de passe</label>
                <input
                    type="password"
                    name="motDePasse"
                    required
                    minLength={6}
                    value={formData.motDePasse}
                    onChange={handleChange}
                    className={inputClass}
                />
            </div>

            {/* Confirmer mot de passe */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1"> 🔑 Confirmer votre mot de passe</label>
                <input
                    type="password"
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className={inputClass}
                />
            </div>

            {/* Rôle (lecture seule) */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">🏷️ Rôle</label>
                <input
                    type="text"
                    name="role"
                    disabled
                    value={formData.role}
                    className={`${inputClass} opacity-50 cursor-not-allowed`}
                />
            </div>

            {/* Message d'erreur */}
            {error && (
                <p className="text-red-400 text-xs text-center font-semibold bg-red-500/10 py-2 rounded-lg border border-red-500/20">
                   ⚠️ {error}
                </p>
            )}

            {/* Bouton soumission */}
            <button
                type="submit"
                disabled={isLoading}
                className="mt-4 bg-brand-amber hover:bg-amber-500 text-brand-dark font-bold py-3 rounded-lg uppercase tracking-tighter transition-transform active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
            >
                {isLoading ? 'Création en cours...' : 'Créer un compte'}
            </button>
        </form>
    );
};

export default RegisterForm;