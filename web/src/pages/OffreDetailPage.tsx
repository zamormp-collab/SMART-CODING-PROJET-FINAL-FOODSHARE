import React, { useEffect, useState } from 'react';
import { Offre } from '../types/index';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { getOffreById, annulerOffre } from '../services/OffreService';

function formatHeure(iso: string): string {
    return new Date(iso).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
}
function formatDateComplete(iso: string): string {
    return new Date(iso).toLocaleDateString('fr-FR', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric',
    });
}
function pourcentageRestant(initiale: number, restante: number): number {
    if (initiale === 0) return 0;
    return Math.round((restante / initiale) * 100);
}

const STATUT_CONFIG = {
    ACTIVE: { label: '● Active', className: 'bg-green-500/80 text-white' },
    EXPIREE: { label: '● Expirée', className: 'bg-gray-700/80 text-gray-300' },
    ANNULEE: { label: '● Annulée', className: 'bg-red-700/80 text-red-200' },
};

const OffreDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const location = useLocation();


    const [offre, setOffre] = useState<Offre | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [annulation, setAnnulation] = useState(false);

    useEffect(() => {
        if (!id) return;

        // Si l'offre est passée via navigate(state) → on l'utilise directement
        const offrePassee = (location.state as any)?.offre as Offre | undefined;
        if (offrePassee && offrePassee.id === Number(id)) {
            setOffre(offrePassee);
            setLoading(false);
            return;
        }

        // Sinon appel API normal (vraies offres backend, accès direct par URL)
        setLoading(true);
        getOffreById(Number(id))
            .then(data => setOffre(data))
            .catch(() => setError('Offre introuvable'))
            .finally(() => setLoading(false));
    }, [id, location.state]);

    const handleAnnuler = async () => {
        if (!offre) return;
        if (!window.confirm(`Annuler définitivement "${offre.titre}" ?\nLes étudiants ne pourront plus la réserver.`)) return;
        setAnnulation(true);
        try {
            await annulerOffre(offre.id);
            navigate('/dashboard');
        } catch (err: any) {
            alert(err.message || "Impossible d'annuler cette offre.");
        } finally {
            setAnnulation(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-[#1e130c] flex items-center justify-center">
                <div className="w-8 h-8 border-2 border-amber-400/30 border-t-amber-400 rounded-full animate-spin" />
            </div>
        );
    }

    if (error || !offre) {
        return (
            <div className="min-h-screen bg-[#1e130c] flex flex-col items-center justify-center gap-4">
                <span className="text-5xl"></span>
                <p className="text-white font-semibold text-lg">Offre introuvable</p>
                <button onClick={() => navigate('/dashboard')} className="bg-amber-400 text-[#1e130c] font-bold px-6 py-2.5 rounded-xl">
                    Retour au dashboard
                </button>
            </div>
        );
    }

    const statutCfg = STATUT_CONFIG[offre.statutOffre] ?? STATUT_CONFIG.EXPIREE;
    const estActive = offre.statutOffre === 'ACTIVE';
    const pct = pourcentageRestant(offre.quantiteInitiale, offre.quantiteRestante);
    const couleurStock = pct > 60 ? 'bg-green-500' : pct > 30 ? 'bg-amber-400' : 'bg-red-500';

    return (
        <div className="min-h-screen bg-[#1e130c]">
            <nav className="sticky top-0 z-10 bg-[#1e130c]/95 backdrop-blur border-b border-white/5 px-6 py-3">
                <div className="max-w-4xl mx-auto flex items-center gap-4">
                    <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-gray-400 hover:text-white text-sm transition-colors">
                        ← Retour
                    </button>
                    <span className="text-white font-black text-sm tracking-wider uppercase">FoodShare</span>
                </div>
            </nav>

            <main className="max-w-4xl mx-auto px-6 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

                    {/* Colonne gauche */}
                    <div className="flex flex-col gap-4">
                        <div className="w-full h-72 rounded-2xl overflow-hidden relative">
                            {offre.imageUrl ? (
                                <img src={offre.imageUrl} alt={offre.titre} className="w-full h-full object-cover"
                                    onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                            ) : (
                                <div className="w-full h-full bg-[#3d281a] flex items-center justify-center">
                                    <span className="text-7xl">🍽️</span>
                                </div>
                            )}
                            <div className="absolute top-3 right-3">
                                <span className={`text-xs font-bold uppercase px-3 py-1.5 rounded-full backdrop-blur-sm ${statutCfg.className}`}>
                                    {statutCfg.label}
                                </span>
                            </div>
                        </div>

                        <div className="bg-[#3d281a] border border-white/5 rounded-2xl p-5">
                            <h3 className="text-white font-semibold text-sm mb-4">Disponibilité</h3>
                            <div className="flex justify-between text-sm mb-2">
                                <span className="text-gray-400">Portions restantes</span>
                                <span className="text-white font-bold">
                                    {offre.quantiteRestante}
                                    <span className="text-gray-400 font-normal"> / {offre.quantiteInitiale}</span>
                                </span>
                            </div>
                            <div className="h-3 bg-white/10 rounded-full overflow-hidden mb-3">
                                <div className={`h-full rounded-full ${couleurStock}`} style={{ width: `${pct}%` }} />
                            </div>
                            <div className="flex justify-between text-xs text-gray-400">
                                <span>{pct}% disponible</span>
                                <span>{offre.quantiteInitiale - offre.quantiteRestante} réservées</span>
                            </div>
                        </div>
                    </div>

                    {/* Colonne droite */}
                    <div className="flex flex-col gap-5">
                        <div className="flex items-start justify-between gap-4">
                            <h1 className="text-white font-black text-2xl leading-tight">{offre.titre}</h1>
                            {offre.prix === 0 ? (
                                <span className="bg-green-500/20 text-green-400 font-black text-lg px-4 py-1.5 rounded-xl flex-shrink-0">Gratuit</span>
                            ) : (
                                <span className="bg-amber-400/20 text-amber-400 font-black text-lg px-4 py-1.5 rounded-xl flex-shrink-0">
                                    {Number(offre.prix).toFixed(2)} $
                                </span>
                            )}
                        </div>

                        <div className="bg-[#3d281a] border border-white/5 rounded-2xl p-5">
                            <h3 className="text-gray-400 text-xs uppercase font-bold mb-2 tracking-wider">Description</h3>
                            <p className="text-gray-200 text-sm leading-relaxed">{offre.description}</p>
                        </div>

                        <div className="bg-[#3d281a] border border-white/5 rounded-2xl p-5">
                            <h3 className="text-gray-400 text-xs uppercase font-bold mb-3 tracking-wider">Créneau de retrait</h3>
                            <div className="flex flex-col gap-2">
                                <div className="flex items-center gap-3">
                                    <span className="text-lg">📅</span>
                                    <p className="text-white text-sm font-medium capitalize">{formatDateComplete(offre.debutRetrait)}</p>
                                </div>
                                <div className="flex items-center gap-3">
                                    <span className="text-lg">🕐</span>
                                    <p className="text-white text-sm font-medium">
                                        {formatHeure(offre.debutRetrait)}
                                        <span className="text-gray-400 mx-2">→</span>
                                        {formatHeure(offre.finRetrait)}
                                    </p>
                                </div>
                                <div className="flex items-center gap-3">
                                    <span className="text-lg">📍</span>
                                    <p className="text-white text-sm font-medium">{offre.lieu}</p>
                                </div>
                            </div>
                        </div>

                        {/* Actions — seulement si ACTIVE */}
                        {estActive && (
                            <>
                                <button
                                    onClick={() => navigate(`/offres/${offre.id}/reservations`)}
                                    className="w-full bg-amber-400 hover:bg-amber-500 text-[#1e130c] font-bold py-3.5 rounded-xl transition-all active:scale-95 text-sm"
                                >
                                    📋 Voir les réservations reçues
                                </button>

                                <button
                                    onClick={() => navigate(`/offres/${offre.id}/modifier`)}
                                    className="w-full bg-white/5 hover:bg-white/10 text-white font-semibold py-3 rounded-xl transition-colors text-sm border border-white/10"
                                >
                                    ✏️ Modifier cette offre
                                </button>

                                <button
                                    onClick={handleAnnuler}
                                    disabled={annulation}
                                    className="w-full bg-red-500/10 hover:bg-red-500/20 text-red-400 font-semibold py-3 rounded-xl transition-colors text-sm disabled:opacity-50"
                                >
                                    {annulation ? 'Annulation…' : '🚫 Annuler cette offre'}
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
};

export default OffreDetailPage;