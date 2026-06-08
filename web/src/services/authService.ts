import { apiFetch } from './api';
import { LoginRequest, RegisterRequest, AuthResponse } from '../types';

export const authService = {

    // Connexion -> Cible : /api/auth/connexion
    login: async (credentials: LoginRequest): Promise<AuthResponse> => {
        return apiFetch<AuthResponse>('/auth/connexion', {
            method: 'POST',
            body: JSON.stringify({
                email: credentials.email,
                motDePasse: credentials.password, 
            }),
        });
    },

    // Inscription -> Cible : /api/auth/inscription
    register: async (userData: RegisterRequest): Promise<AuthResponse> => {
        return apiFetch<AuthResponse>('/auth/inscription', {
            method: 'POST',
            body: JSON.stringify(userData),
        });
    },

    // Vérification E-mail -> Cible : /api/auth/verification-email?email=...
    verifyEmail: async (email: string): Promise<boolean> => {
        const encodedEmail = encodeURIComponent(email);
        try {
            const result = await apiFetch<boolean>(`/auth/verification-email?email=${encodedEmail}`, {
                method: 'GET',
            });
            return result;
        } catch {
            return false;
        }
    }
};