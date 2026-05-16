import React, { useState, ChangeEvent, FormEvent } from 'react';

// Définition des champs requis selon le modèle de données du sujet
interface RegisterData {
    nom: string;
    prenom: string;
    email: string;
    motDePasse: string;

}

const RegisterForm: React.FC = () => {
    const [formData, setFormData] = useState<RegisterData>({
        nom: '',
        prenom: '',
        email: '',
        motDePasse: '',
    });

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        // Logique d'appel vers l'API Spring Boot à venir
        console.log("Données d'inscription :", formData);
    };

    return (
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <h2 className="text-brand-amber text-center font-bold text-xl uppercase mb-2">
                Inscription
            </h2>

            {/* Nom  */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Nom</label>
                <input
                    type="text"
                    name="nom"
                    required
                    value={formData.nom}
                    onChange={handleChange}
                     className="w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all"
                />
            </div>

                  {/* prenom */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Prenom</label>
                <input
                    type="text"
                    name="prenom"
                    required
                    value={formData.nom}
                    onChange={handleChange}
                     className="w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all"
                />
            </div>

            {/* Email */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Email</label>
                <input
                    type="email"
                    name="email"
                    required
                    value={formData.email}
                    onChange={handleChange}
                     className="w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all"
                    placeholder=" "
                />
            </div>

           
            {/* Mot de Passe */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Mot de passe</label>
                <input
                    type="password"
                    name="motDePasse"
                    required
                    value={formData.motDePasse}
                    onChange={handleChange}
                     className="w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all"
                    placeholder=" "
                />
            </div>

            <button
                type="submit"
                className="mt-4 bg-brand-amber hover:bg-amber-500 text-brand-dark font-bold py-3 rounded-lg uppercase tracking-tighter transition-transform active:scale-95"
            >
                Créer un compte
            </button>
        </form>
    );
};

export default RegisterForm;