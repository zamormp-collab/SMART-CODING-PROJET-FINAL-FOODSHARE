import React, { createContext, useState, useContext, useEffect } from 'react';
import { LoginRequest, AuthResponse, User } from '../types';
import { authService } from '../services/authService';

interface AuthContextType {
  user: User | null;
  token: string | null;
  loginUser: (credentials: LoginRequest) => Promise<void>;
  logoutUser: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('foodshare_token'));
  const [user, setUser] = useState<User | null>(() => {
    // FIX: restaurer l'utilisateur depuis localStorage au démarrage
    const stored = localStorage.getItem('foodshare_user');
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('foodshare_token');
    const storedUser = localStorage.getItem('foodshare_user');
    if (storedToken) setToken(storedToken);
    if (storedUser) setUser(JSON.parse(storedUser));
    setLoading(false);
  }, []);

  const loginUser = async (credentials: LoginRequest) => {
    // FIX: AuthResponse retourne des champs plats (pas d'objet "user" imbriqué)
    // On reconstruit le User localement
    const data: AuthResponse = await authService.login(credentials);

    const userObj: User = {
      id: data.userId ?? 0,
      email: data.email,
      nom: data.nom,
      prenom: data.prenom,
      role: data.role,
      adresse: '',
      telephone: '',
      password: '',
    };

    setToken(data.token);
    setUser(userObj);
    localStorage.setItem('foodshare_token', data.token);
    localStorage.setItem('foodshare_user', JSON.stringify(userObj));
  };

  const logoutUser = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('foodshare_token');
    localStorage.removeItem('foodshare_user');
  };

  return (
    <AuthContext.Provider value={{ user, token, loginUser, logoutUser, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth doit être utilisé dans un AuthProvider");
  return context;
};

export { useAuth };