import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useOffres } from '../hooks/useOffres';
import { useAuth } from '../Context/AuthContext';

// ── Calcul de la date minimum recalculé à chaque render ───
function calculerDateMin(): string {
    const maintenant = new Date();
    maintenant.setMinutes(maintenant.getMinutes() - 5);
    const tzOffset = maintenant.getTimezoneOffset() * 60000;
    return new Date(maintenant.getTime() - tzOffset).toISOString().slice(0, 16);
}

const CreerOffrePage: React.FC = () => {
    const navigate = useNavigate();
    const { ajouterOffre } = useOffres();
    const { user, logoutUser } = useAuth();

    // Recalculé à chaque render → toujours à jour
    const dateMin = calculerDateMin();

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [imageError, setImageError] = useState(false);

    const [form, setForm] = useState({
        titre: '',
        description: '',
        quantite: 1,
        prix: 0,
        debutRetrait: '',
        finRetrait: '',
        lieu: '',
        imageUrl: '',
    });

    // ── Gestion des changements de champs ─────────
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;

        // Réinitialise l'erreur d'aperçu si on modifie l'URL image
        if (name === 'imageUrl') setImageError(false);

        // Quand le début change, on remet fin à vide si elle devient invalide
        if (name === 'debutRetrait') {
            setForm(prev => ({
                ...prev,
                debutRetrait: value,
                // On conserve finRetrait seulement si elle reste après le nouveau début
                finRetrait: prev.finRetrait && prev.finRetrait > value
                    ? prev.finRetrait
                    : '',
            }));
            return;
        }

        setForm(prev => ({
            ...prev,
            [name]: type === 'number' ? parseFloat(value) || 0 : value,
        }));
    };

    // ── Soumission du formulaire ───
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Validation des champs obligatoires
        if (!form.titre.trim() || !form.description.trim() || !form.lieu.trim()) {
            setError('Veuillez remplir tous les champs obligatoires (titre, description, lieu).');
            return;
        }
        if (!form.debutRetrait || !form.finRetrait) {
            setError('Veuillez renseigner les dates de début et de fin de retrait.');
            return;
        }
        if (new Date(form.debutRetrait) < new Date(calculerDateMin())) {
            setError('Le début du créneau ne peut pas être dans le passé.');
            return;
        }
        if (new Date(form.finRetrait) <= new Date(form.debutRetrait)) {
            setError('La date de fin de retrait doit être après la date de début.');
            return;
        }
        if (form.quantite < 1) {
            setError('La quantité doit être au moins 1.');
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            // imageUrl est optionnel — on l'envoie seulement si renseigné
            await ajouterOffre({
                ...form,
                debutRetrait: new Date(form.debutRetrait).toISOString(),
                finRetrait: new Date(form.finRetrait).toISOString(),
                imageUrl: form.imageUrl.trim() || undefined,
            });

            // Redirection vers le dashboard
            navigate('/dashboard');
        } catch (err: any) {
            setError(err.message || "Impossible de créer l'offre.");
        } finally {
            setIsLoading(false);
        }
    };
    // Initiales de l'utilisateur connecté
    const initiales = user && user.prenom && user.nom
        ? `${user.prenom.charAt(0)}${user.nom.charAt(0)}`.toUpperCase()
        : 'U';

    return (
        <div className="min-h-screen bg-[#1b100a] text-white font-sans relative">

            {/* Navbar */}
            <nav className="w-full px-8 py-5 flex items-center justify-between bg-[#1b100a]/95 backdrop-blur-md border-b border-white/5 fixed top-0 left-0 z-50">
                <div
                    className="flex items-center gap-3 cursor-pointer"
                    onClick={() => navigate('/dashboard')}
                >
                    <img
                        src="/logo-foodshare.png"
                        alt="FoodShare Logo"
                        className="w-8 h-8 object-contain"
                        onError={(e) => { e.currentTarget.style.display = 'none'; }}
                    />
                    <span className="font-black text-xl tracking-tighter uppercase italic">FoodShare</span>
                </div>

                <div className="flex items-center gap-6">
                    {/* Avatar + nom */}
                    <div className="flex items-center gap-2.5 bg-white/5 pl-2 pr-4 py-1.5 rounded-full border border-white/10">
                        <div className="w-8 h-8 rounded-full bg-[#d98236] flex items-center justify-center text-[#1b100a] font-black text-xs">
                            {initiales}
                        </div>
                        <span className="text-xs font-bold text-gray-200">
                            {user ? `${user.prenom} ${user.nom}` : 'Utilisateur'}
                        </span>
                    </div>
                    {/* Déconnexion */}
                    <button
                        onClick={logoutUser}
                        className="text-xs font-black bg-white/5 border border-white/10 rounded-xl px-5 py-2 hover:bg-white/10 transition-all uppercase tracking-widest"
                    >
                        Déconnexion
                    </button>
                </div>
            </nav>

            {/*  Contenu principal  */}
            <main className="w-full px-8 md:px-16 pt-28 pb-24">
                <div className="mb-8">
                    <h1 className="text-2xl font-black tracking-tight">Créer une offre</h1>
                    <p className="text-gray-400 text-xs mt-1">
                        Partagez vos invendus alimentaires avec les étudiants.
                    </p>
                </div>

                {/* Bloc erreur */}
                {error && (
                    <div className="mb-6 flex items-start gap-3 bg-red-500/10 border border-red-500/30 text-red-400 text-xs py-3 px-5 rounded-xl w-full">
                        <span className="bg-red-500 text-[#1b100a] rounded-full w-4 h-4 flex items-center justify-center font-black shrink-0 text-[10px]">
                            !
                        </span>
                        <p>{error}</p>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="flex flex-col gap-6 w-full">

                    {/* Titre */}
                    <Field label="Titre *">
                        <input
                            name="titre"
                            type="text"
                            value={form.titre}
                            onChange={handleChange}
                            placeholder="Ex : Plateaux repas du midi – 5 restants"
                            className={inputClass}
                        />
                    </Field>

                    {/* Description */}
                    <Field label="Description *">
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            rows={4}
                            placeholder="Ingrédients, allergènes, état du produit…"
                            className={`${inputClass} resize-none`}
                        />
                    </Field>

                    {/* Quantité + Prix */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <Field label="Quantité *">
                            <input
                                name="quantite"
                                type="number"
                                min={1}
                                value={form.quantite}
                                onChange={handleChange}
                                className={inputClass}
                            />
                        </Field>

                        <Field label="Prix">
                            <div className="relative">
                                <input
                                    name="prix"
                                    type="number"
                                    min={0}
                                    step={0.01}
                                    value={form.prix}
                                    onChange={handleChange}
                                    className={inputClass}
                                />
                                <span className="absolute right-4 top-1/2 -translate-y-1/2 text-[10px] font-black text-gray-500 uppercase">
                                    $
                                </span>
                            </div>
                        </Field>
                    </div>

                    {/* Lieu */}
                    <Field label="Lieu de retrait *">
                        <input
                            name="lieu"
                            type="text"
                            value={form.lieu}
                            onChange={handleChange}
                            placeholder="Ex : Cafétéria Bâtiment A, guichet 2"
                            className={inputClass}
                        />
                    </Field>

                    {/*  Dates de retrait  */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

                        {/* Début */}
                        <Field label="Début retrait *">
                            <input
                                name="debutRetrait"
                                type="datetime-local"
                                value={form.debutRetrait}
                                onChange={handleChange}
                                min={dateMin}
                                className={inputClass}
                            />
                        </Field>

                        {/* Fin — activé seulement après choix du début */}
                        <Field label="Fin retrait *">
                            <input
                                name="finRetrait"
                                type="datetime-local"
                                value={form.finRetrait}
                                onChange={handleChange}
                                // min = début choisi → empêche fin <= début
                                min={form.debutRetrait || dateMin}
                                disabled={!form.debutRetrait}
                                className={`${inputClass} ${!form.debutRetrait ? 'opacity-40 cursor-not-allowed' : ''}`}
                            />
                            {/* Aide contextuelle si début pas encore choisi */}
                            {!form.debutRetrait && (
                                <p className="text-gray-600 text-[10px] ml-1 mt-1">
                                    Choisissez d'abord la date de début
                                </p>
                            )}
                        </Field>
                    </div>

                    {/*  URL image avec aperçu en temps réel  */}
                    <Field label="URL de l'image (optionnel — format PNG recommandé)">
                        <input
                            name="imageUrl"
                            type="text"
                            value={form.imageUrl}
                            onChange={handleChange}
                            placeholder="https://images.unsplash.com/photo-xxx?w=400&q=80&fm=png"
                            className={inputClass}
                        />
                        {/* Aperçu si URL renseignée et valide */}
                        {form.imageUrl.trim() && !imageError && (
                            <div className="mt-2 rounded-xl overflow-hidden border border-white/10 h-36 w-full">
                                <img
                                    src={form.imageUrl.trim()}
                                    alt="Aperçu"
                                    className="w-full h-full object-cover"
                                    onError={() => setImageError(true)}
                                />
                            </div>
                        )}
                        {/* Message si l'URL est invalide */}
                        {form.imageUrl.trim() && imageError && (
                            <p className="text-red-400 text-[10px] ml-1 mt-1">
                                ⚠️ L'image n'a pas pu être chargée. Vérifiez l'URL.
                            </p>
                        )}
                    </Field>

                    {/*  Boutons Annuler / Publier  */}
                    <div className="flex flex-col md:flex-row gap-4 mt-4">
                        <button
                            type="button"
                            onClick={() => navigate('/dashboard')}
                            className="flex-1 py-3.5 rounded-xl border-2 border-white/10 text-gray-300 font-black uppercase tracking-widest hover:bg-white/5 transition-all text-xs"
                        >
                            Annuler
                        </button>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="flex-1 py-3.5 rounded-xl bg-[#d98236] hover:bg-[#c4712b] text-[#1b100a] font-black uppercase tracking-widest transition-all disabled:opacity-50 text-xs"
                        >
                            {isLoading ? 'Publication en cours…' : "Publier l'offre"}
                        </button>
                    </div>
                </form>
            </main>

            {/* Bouton retour rond flottant en bas à droite */}
            <button
                onClick={() => navigate('/dashboard')}
                className="
                    fixed bottom-10 right-10 w-14 h-14
                    bg-[#d98236] text-[#1b100a] rounded-full
                    flex items-center justify-center
                    shadow-2xl hover:scale-110 active:scale-95
                    transition-all z-[100] border-4 border-[#1b100a]
                    cursor-pointer
                "
                title="Retour au tableau de bord"
            >
                <span className="text-xl font-black">←</span>
            </button>
        </div>
    );
};

//  Wrapper label + champ pour cohérence visuelle 
const Field: React.FC<{ label: string; children: React.ReactNode }> = ({ label, children }) => (
    <div className="flex flex-col gap-2 w-full">
        <label className="text-gray-400 text-[10px] font-black uppercase tracking-widest ml-1">
            {label}
        </label>
        {children}
    </div>
);

// Classe CSS commune à tous les inputs 
const inputClass =
    'w-full border-2 border-white/5 rounded-xl px-5 py-3 text-white bg-white/[0.03] outline-none focus:border-[#d98236] focus:bg-white/[0.07] transition-all placeholder:text-gray-700 text-sm font-medium';

export default CreerOffrePage;