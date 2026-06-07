import React, { useState } from 'react';
import LoginForm from './LoginForm';
import RegisterForm from './RegisterForm';

const AuthContainer: React.FC = () => {
    const [isLogin, setIsLogin] = useState(true);

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#1e130c] p-4">
            <div className="w-full max-w-md bg-[#4a3321] rounded-2xl shadow-2xl p-8 flex flex-col items-center border border-white/5">

                {/* Section Logo et Titres */}
                <div className="mb-6 flex flex-col items-center text-center">
                    <div className="bg-white rounded-3xl mb-3 shadow-md w-28 h-28 flex items-center justify-center overflow-hidden p-2">
                        <img
                            src="logo-foodshare.png"
                            alt="FoodShare Logo"
                            className="w-full h-full object-contain"
                        />
                    </div>

                    <h1 className="text-white font-black text-2xl tracking-wider uppercase">
                        FOOD SHARE
                    </h1>
                    <p className="text-gray-300/80 text-xs mt-1 font-medium">
                        Connectez-vous à votre compte
                    </p>
                </div>

                {/* Formulaire dynamique (Login ou Inscription) */}
                <div className="w-full bg-[#3d281a] p-6 rounded-xl border border-white/5 shadow-inner">
                    {isLogin ? <LoginForm /> : <RegisterForm />}
                </div>


                <button
                    onClick={() => setIsLogin(!isLogin)}
                    className="mt-6 text-gray-400 text-xs hover:text-brand-amber transition-colors font-medium"
                >
                    {isLogin
                        ? "Vous n'avez pas de compte ? Créer un compte"
                        : "Déjà inscrit ? Se connecter à votre compte"
                    }
                </button>
            </div>
        </div>
    );
};

export default AuthContainer;