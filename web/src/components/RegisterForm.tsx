import React, { useState, ChangeEvent, FormEvent } from 'react';

// Définition des champs requis pour le formulaire
interface RegisterData {
    nom: string;
    prenom: string;
    email: string;
    motDePasse: string;
    role: '';
    address: string;
    Telephone: string;
}

// Etat du formulaire 
const RegisterForm: React.FC = () => {
    const [formData, setFormData] = useState<RegisterData>({
        nom: '',
        prenom: '',
        email: '',
        motDePasse: '',
        role: '',
        address: '',
        Telephone: '',
    });

    //Champs qui sert uniquement a la validation cote client
    const [ConfirmPassword, setConfirmePassword] = useState('');

    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    //Gestionnaire de changement generique
    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    //Soumission du formulaire d'inscription
    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();

        //Validation du mot de passe
        if (formData.motDePasse !== ConfirmPassword) {
            setError('Les mots de passe ne correspondent pas.');
            return;
        }
        setIsLoading(true);
        setError(null);

        try {
            // Logique d'appel vers l'API Spring Boot //
            console.log("Données d'inscription :", formData);
        } catch {
            setError("Erreur lors de l'inscription. Cet email est peut deja utiliser.");
        } finally {
            setIsLoading(false)
        }
    };

    // Classes CSS communes aux inputs (évite la répétition)
    const inputClass = "w-full border border-white/10 rounded-lg px-4 py-2 text-white outline-none focus:border-brand-amber transition-all bg-transparent";
    const labelClass = "text-gray-400 text-[10px] uppercase font-bold ml-1";

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
                    className={inputClass}
                />
            </div>

            {/* prenom */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Prenom</label>
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
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Email</label>
                <input
                    type="email"
                    name="email"
                    required
                    value={formData.email}
                    onChange={handleChange}
                    placeholder=" "
                    className={inputClass}
                />
            </div>

            {/* Telephone */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Telephone</label>
                <input
                    type="tel"
                    name="telephone"
                    required
                    value={formData.Telephone}
                    onChange={handleChange}
                    placeholder=" "
                    className={inputClass}
                />
            </div>

            {/* Adresse */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Adresse</label>
                <input
                    type="text"
                    name="address"
                    required
                    value={formData.address}
                    onChange={handleChange}
                    placeholder=" "
                    className={inputClass}
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
                    placeholder=" "
                    className={inputClass}
                />
            </div>


            {/* Role */}
            <div className="flex flex-col gap-1">
                <label className="text-gray-400 text-[10px] uppercase font-bold ml-1">Role</label>
                <input
                    type="role"
                    name="role"
                    required
                    value={formData.role}
                    onChange={handleChange}
                    placeholder=" "
                    className={inputClass}
                />
            </div>

            {/* Message d'erreur */}
            {error && (
                <p className="text-red-400 text-xs text-center">{error}</p>
            )}

            {/*  Bouton soumission  */}
            <button
                type="submit"
                disabled={isLoading}
                className="mt-4 bg-brand-amber hover:bg-amber-500 text-brand-dark font-bold py-3 rounded-lg uppercase tracking-tighter transition-transform active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed" >
                {isLoading ? 'Création en cours...' : 'Créer un compte'}
            </button>

        </form>
    );
};

export default RegisterForm;