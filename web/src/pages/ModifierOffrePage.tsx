import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useOffres } from '../hooks/useOffres';
import { useAuth } from '../Context/AuthContext';
import { getOffreById } from '../services/OffreService';
import { Offre } from '../types/index';

/**
 * Convertit une date ISO (retournée par le backend) en format attendu
 * par les inputs datetime-local : "YYYY-MM-DDTHH:mm"
 */
function isoVersInputDatetime(iso: string | undefined): string {
    if (!iso) return '';
    try {
        const d = new Date(iso);
        const tzOffset = d.getTimezoneOffset() * 60000;
        return new Date(d.getTime() - tzOffset).toISOString().slice(0, 16);
    } catch {
        return '';
    }
}

/** Date minimum pour les champs datetime-local, calculée une seule fois. */
function calculerDateMin(): string {
    const maintenant = new Date();
    maintenant.setMinutes(maintenant.getMinutes() - 5);
    const tzOffset = maintenant.getTimezoneOffset() * 60000;
    return new Date(maintenant.getTime() - tzOffset).toISOString().slice(0, 16);
}

// ModifierOffrePage
const ModifierOffrePage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { modifierOffre } = useOffres();
    const { user, logoutUser } = useAuth();

    // dateMin fixe — voir commentaire dans CreerOffrePage
    const dateMin = useRef<string>(calculerDateMin()).current;

    const [offre, setOffre] = useState<Offre | null>(null);
    const [chargement, setChargement] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

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

    //  Chargement initial de l'offre
    useEffect(() => {
        if (!id) return;
        setChargement(true);
        getOffreById(Number(id))
            .then(data => {
                // Règle MVP : seules les offres ACTIVE sont modifiables
                if (data.statutOffre !== 'ACTIVE') {
                    navigate('/dashboard');
                    return;
                }
                setOffre(data);
                // Pré-remplissage du formulaire avec les valeurs existantes
                setForm({
                    titre: data.titre,
                    description: data.description,
                    quantite: data.quantiteInitiale,
                    prix: Number(data.prix),
                    debutRetrait: isoVersInputDatetime(data.debutRetrait),
                    finRetrait: isoVersInputDatetime(data.finRetrait),
                    lieu: data.lieu,
                    imageUrl: data.imageUrl ?? '',
                });
            })
            .catch(() => setError('Offre introuvable ou accès refusé.'))
            .finally(() => setChargement(false));
    }, [id, navigate]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === 'number' ? parseFloat(value) || 0 : value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!form.titre.trim() || !form.description.trim() || !form.lieu.trim()) {
            setError('Veuillez remplir tous les champs obligatoires.');
            return;
        }
        if (!form.debutRetrait || !form.finRetrait) {
            setError('Veuillez renseigner les dates de début et de fin de retrait.');
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
            await modifierOffre(Number(id), {
                ...form,
                imageUrl: form.imageUrl.trim() || undefined,
            });
            // Retour au détail après modification réussie
            navigate(`/offres/${id}`);
        } catch (err: any) {
            setError(err.message || "Impossible de modifier l'offre.");
        } finally {
            setIsLoading(false);
        }
    };

    const initiales = user && user.prenom && user.nom
        ? `${user.prenom.charAt(0)}${user.nom.charAt(0)}`.toUpperCase()
        : 'U';

    // États de chargement / erreur 
    if (chargement) {
        return (
            <div className="min-h-screen bg-[#1b100a] flex items-center justify-center">
                <div className="w-8 h-8 border-2 border-amber-400/30 border-t-amber-400 rounded-full animate-spin" />
            </div>
        );
    }

    if (error && !offre) {
        return (
            <div className="min-h-screen bg-[#1b100a] flex flex-col items-center justify-center gap-4">
                <span className="text-5xl"></span>
                <p className="text-white font-semibold">{error}</p>
                <button
                    onClick={() => navigate('/dashboard')}
                    className="bg-amber-400 text-[#1b100a] font-bold px-6 py-2.5 rounded-xl"
                >
                    Retour au dashboard
                </button>
            </div>
        );
    }

    //  Rendu principal 
    return (
        <div className="min-h-screen bg-[#1b100a] text-white font-sans relative">

            {/*  Navbar */}
            <nav className="w-full px-8 py-5 flex items-center justify-between bg-[#1b100a]/95 backdrop-blur-md border-b border-white/5 fixed top-0 left-0 z-50">
                <div
                    className="flex items-center gap-3 cursor-pointer"
                    onClick={() => navigate(`/offres/${id}`)}
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
                    <div className="flex items-center gap-2.5 bg-white/5 pl-2 pr-4 py-1.5 rounded-full border border-white/10">
                        <div className="w-8 h-8 rounded-full bg-[#d98236] flex items-center justify-center text-[#1b100a] font-black text-xs">
                            {initiales}
                        </div>
                        <span className="text-xs font-bold text-gray-200">
                            {user?.prenom} {user?.nom}
                        </span>
                    </div>
                    <button
                        onClick={logoutUser}
                        className="text-xs font-black bg-white/5 border border-white/10 rounded-xl px-5 py-2 hover:bg-white/10 transition-all uppercase tracking-widest"
                    >
                        Déconnexion
                    </button>
                </div>
            </nav>

            {/*Formulaire  */}
            <main className="w-full px-8 md:px-16 pt-28 pb-12">
                <div className="mb-8">
                    <button
                        onClick={() => navigate(`/offres/${id}`)}
                        className="text-gray-400 hover:text-white text-xs mb-3 flex items-center gap-1 transition-colors"
                    >
                        ← Retour au détail
                    </button>
                    <h1 className="text-2xl font-black tracking-tight">Modifier l'offre</h1>
                    <p className="text-gray-400 text-xs mt-1">
                        {offre?.titre}
                    </p>
                </div>

                {error && (
                    <div className="mb-6 flex items-start gap-3 bg-red-500/10 border border-red-500/30 text-red-400 text-xs py-3 px-5 rounded-xl">
                        <span className="bg-red-500 text-[#1b100a] rounded-full w-4 h-4 flex items-center justify-center font-black shrink-0 text-[10px]">!</span>
                        <p>{error}</p>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="flex flex-col gap-6 w-full">

                    <Field label="Titre *">
                        <input name="titre" type="text" value={form.titre} onChange={handleChange}
                            placeholder="Titre de l'offre" className={inputClass} />
                    </Field>

                    <Field label="Description *">
                        <textarea name="description" value={form.description} onChange={handleChange}
                            rows={4} placeholder="Description détaillée" className={`${inputClass} resize-none`} />
                    </Field>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <Field label="Quantité *">
                            <input name="quantite" type="number" min={1} value={form.quantite}
                                onChange={handleChange} className={inputClass} />
                        </Field>

                        <Field label="Prix (HTG) — 0 = don gratuit *">
                            <div className="relative">
                                <input name="prix" type="number" min={0} step={0.01} value={form.prix}
                                    onChange={handleChange} className={inputClass} />
                                <span className="absolute right-4 top-1/2 -translate-y-1/2 text-[10px] font-black text-gray-500 uppercase">HTG</span>
                            </div>
                        </Field>
                    </div>

                    <Field label="Lieu de retrait *">
                        <input name="lieu" type="text" value={form.lieu} onChange={handleChange}
                            placeholder="Ex : Cafétéria Bâtiment A" className={inputClass} />
                    </Field>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                        <Field label="Début retrait *">
                            <input name="debutRetrait" type="datetime-local" value={form.debutRetrait}
                                onChange={handleChange} min={dateMin} className={inputClass} />
                        </Field>
                        <Field label="Fin retrait *">
                            <input name="finRetrait" type="datetime-local" value={form.finRetrait}
                                onChange={handleChange} min={form.debutRetrait || dateMin} className={inputClass} />
                        </Field>
                    </div>

                    {/* FIX IMAGE : type="text" — voir commentaire dans CreerOffrePage */}
                    <Field label="URL de l'image (optionnel)">
                        <input name="imageUrl" type="text" value={form.imageUrl} onChange={handleChange}
                            placeholder="https://exemple.com/image.jpg" className={inputClass} />
                    </Field>

                    <div className="flex flex-col md:flex-row gap-4 mt-4">
                        <button
                            type="button"
                            onClick={() => navigate(`/offres/${id}`)}
                            className="flex-1 py-3.5 rounded-xl border-2 border-white/10 text-gray-300 font-black uppercase tracking-widest hover:bg-white/5 transition-all text-xs"
                        >
                            Annuler
                        </button>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="flex-1 py-3.5 rounded-xl bg-[#d98236] hover:bg-[#c4712b] text-[#1b100a] font-black uppercase tracking-widest transition-all disabled:opacity-50 text-xs"
                        >
                            {isLoading ? 'Sauvegarde…' : '✅ Sauvegarder les modifications'}
                        </button>
                    </div>
                </form>
            </main>

            {/* Bouton retour flottant */}
            <button
                onClick={() => navigate(`/offres/${id}`)}
                className="fixed bottom-10 right-10 w-14 h-14 bg-[#d98236] text-[#1b100a] rounded-full flex items-center justify-center shadow-2xl hover:scale-110 active:scale-95 transition-all z-[100] border-4 border-[#1b100a]"
                title="Retour au détail"
            >
                <span className="text-xl font-black">←</span>
            </button>
        </div>
    );
};

const Field: React.FC<{ label: string; children: React.ReactNode }> = ({ label, children }) => (
    <div className="flex flex-col gap-2 w-full">
        <label className="text-gray-400 text-[10px] font-black uppercase tracking-widest ml-1">{label}</label>
        {children}
    </div>
);

const inputClass =
    'w-full border-2 border-white/5 rounded-xl px-5 py-3 text-white bg-white/[0.03] outline-none focus:border-[#d98236] focus:bg-white/[0.07] transition-all placeholder:text-gray-700 text-sm font-medium';

export default ModifierOffrePage;