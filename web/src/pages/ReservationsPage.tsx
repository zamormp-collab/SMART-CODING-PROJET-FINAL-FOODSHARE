import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Reservation, StatutReservation, Offre } from '../types/index';
import { getReservationsParOffre, marquerRetiree, marquerNonRetiree } from '../services/ReservationServices';
import { getOffreById } from '../services/OffreService';
import { useOffres } from '../hooks/useOffres';
import { useAuth } from '../Context/AuthContext';

//  Formatage de date ISO → "dd mmm yyyy, hh:mm" en français 
function formatDate(iso: string): string {
    return new Date(iso).toLocaleDateString('fr-FR', {
        day: '2-digit', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });
}

//  Config visuelle par statut : label affiché + classes Tailwind du badge ─
const STATUT_CFG: Record<StatutReservation, { label: string; badgeClass: string }> = {
    EN_ATTENTE: { label: '⏳ En attente', badgeClass: 'bg-amber-400/20 text-amber-400' },
    RETIREE: { label: '✅ Retirée', badgeClass: 'bg-green-500/20 text-green-400' },
    NON_RETIREE: { label: '❌ Non retirée', badgeClass: 'bg-red-500/20 text-red-400' },
};

//  Type étendu : associe une réservation à son offre (titre + lieu)
type ReservationAvecOffre = Reservation & {
    offreTitreLocal?: string;
    offreLieuLocal?: string;
};

// ── Items du menu latéral 
const NAV_ITEMS = [
    { label: 'Offres', icon: '🏷️', path: '/dashboard' },
    { label: 'Réservations', icon: '📅', path: '/reservations' },
];

//  Composant principal 
const ReservationsPage: React.FC = () => {
    // Paramètre optionnel :id → mode offre unique (/offres/:id/reservations)
    const { id } = useParams<{ id?: string }>();
    const navigate = useNavigate();
    const { offres } = useOffres();
    const { user, logoutUser } = useAuth();

    // ── États locaux ──
    const [offre, setOffre] = useState<Offre | null>(null);
    const [reservations, setReservations] = useState<ReservationAvecOffre[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [actionId, setActionId] = useState<number | null>(null);          //our
    const [filtreOffre, setFiltreOffre] = useState<number | 'TOUTES'>('TOUTES');

    // ── Initiales de l'utilisateur connecté 
    const initiales = user
        ? `${user.prenom.charAt(0)}${user.nom.charAt(0)}`.toUpperCase()
        : '?';

    // Vérifie si un chemin de nav correspond à la route actuelle
    const isActive = (path: string) => location.pathname.startsWith(path);

    // Déconnexion : vide le contexte et redirige vers /login
    const handleDeconnexion = () => { logoutUser(); navigate('/login'); };

    //  Chargement des réservations au montage / changement d'id 
    useEffect(() => {
        const offreId = id ? Number(id) : NaN;

        if (!isNaN(offreId) && offreId > 0) {
            // Mode offre unique : charge l'offre ET ses réservations en parallèle
            setLoading(true);
            Promise.all([
                getOffreById(offreId),
                getReservationsParOffre(offreId),
            ])
                .then(([offreData, resData]) => {
                    setOffre(offreData);
                    // Enrichit chaque réservation avec titre/lieu de l'offre parente
                    setReservations(resData.map(r => ({
                        ...r,
                        offreTitreLocal: offreData.titre,
                        offreLieuLocal: offreData.lieu,
                    })));
                })
                .catch(err => setError(err.message || 'Erreur de chargement'))
                .finally(() => setLoading(false));

        } else if (offres.length > 0) {
            // Mode "toutes les offres" : charge les réservations de chaque offre en parallèle
            setLoading(true);
            Promise.all(
                offres.map(o =>
                    getReservationsParOffre(o.id)
                        .then(res => res.map(r => ({
                            ...r,
                            offreTitreLocal: o.titre,
                            offreLieuLocal: o.lieu,
                        })))
                        .catch(() => [] as ReservationAvecOffre[])
                )
            )
                .then(resultats => setReservations(resultats.flat()))
                .finally(() => setLoading(false));
        } else {
            // Aucune offre disponible : rien à charger
            setLoading(false);
        }
    }, [id, offres]);

    //  Action : marquer une réservation retirée ou non retirée 
    const marquer = async (resId: number, action: 'retiree' | 'non-retiree') => {
        setActionId(resId);
        try {
            const maj = action === 'retiree'
                ? await marquerRetiree(resId)
                : await marquerNonRetiree(resId);
            // Met à jour uniquement la réservation modifiée dans l'état local
            setReservations(prev => prev.map(r => r.id === resId ? { ...r, ...maj } : r));
        } catch (err: any) {
            alert(err.message || 'Impossible de mettre à jour la réservation.');
        } finally {
            setActionId(null); // Réactive les boutons
        }
    };

    //  Filtrage par offre (actif uniquement en mode "toutes") 
    const reservationsFiltrees = filtreOffre === 'TOUTES'
        ? reservations
        : reservations.filter(r => r.offreId === filtreOffre);

    //  Stats rapides affichées dans les cartes du haut 
    const nbEnAttente = reservations.filter(r => r.statutReservation === 'EN_ATTENTE').length;
    const nbRetirees = reservations.filter(r => r.statutReservation === 'RETIREE').length;

    //  États de chargement et d'erreur 
    if (loading) return (
        <div className="min-h-screen bg-[#1e130c] flex items-center justify-center">
            <div className="w-8 h-8 border-2 border-amber-400/30 border-t-amber-400 rounded-full animate-spin" />
        </div>
    );

    if (error) return (
        <div className="min-h-screen bg-[#1e130c] flex flex-col items-center justify-center gap-4">
            <span className="text-5xl">⚠️</span>
            <p className="text-red-400 font-medium">{error}</p>
            <button
                onClick={() => navigate('/dashboard')}
                className="bg-amber-400 text-[#1e130c] font-bold px-6 py-2.5 rounded-xl cursor-pointer hover:bg-amber-500 transition-all"
            >
                Retour au dashboard
            </button>
        </div>
    );

    // Rendu principal
    return (
        // `relative` sur le conteneur racine pour ancrer le bouton `fixed`
        <div className="flex min-h-screen bg-[#1e130c] relative">

            {/*  Sidebar */}
            <aside className="w-56 min-w-[220px] bg-[#150d07] border-r border-white/5 flex flex-col">

                {/* Logo              */}
                <div className="px-5 py-5 border-b border-white/5">
                    <span className="text-white font-black text-base tracking-wider uppercase flex items-center gap-2">
                        <img
                            src="/logo-foodshare.png"
                            alt="logo FoodShare"
                            className="h-6 w-auto object-contain bg-white rounded-md p-0.5"
                            onError={e => { e.currentTarget.style.display = 'none'; }}
                        />
                        FoodShare
                    </span>
                </div>

                {/* Navigation latérale */}
                <nav className="flex-1 py-4">
                    <p className="px-5 pb-3 text-xs text-white/40 uppercase tracking-widest font-semibold">
                        Menu
                    </p>
                    {NAV_ITEMS.map(item => (
                        <button
                            key={item.path}
                            onClick={() => navigate(item.path)}
                            className={`
                                w-full flex items-center gap-3 px-5 py-3 text-base font-semibold
                                transition-all duration-200 border-l-2 text-left cursor-pointer
                                hover:scale-[1.02] active:scale-95
                                ${isActive(item.path)
                                    ? 'border-amber-400 text-amber-400 bg-amber-400/5'
                                    : 'border-transparent text-white/60 hover:text-white hover:bg-white/5 hover:border-white/20'}
                            `}
                        >
                            <span className="text-lg">{item.icon}</span>
                            {item.label}
                        </button>
                    ))}
                </nav>
            </aside>

            {/*  Zone principale*/}
            <div className="flex-1 flex flex-col overflow-hidden">

                {/* Navbar sticky */}
                <nav className="sticky top-0 z-10 bg-[#1e130c]/95 backdrop-blur border-b border-white/5 px-6 py-3">
                    <div className="flex items-center gap-3">

                        {/* Titre de la section */}
                        <span className="text-white font-black text-sm tracking-wider uppercase">
                            Réservations
                        </span>

                        {/* Avatar, nom complet et bouton de déconnexion */}
                        <div className="flex items-center gap-3 ml-auto">
                            {/* Cercle amber avec initiales */}
                            <div className="w-9 h-9 rounded-full bg-amber-400 flex items-center justify-center">
                                <span className="text-[#1e130c] font-black text-sm">{initiales}</span>
                            </div>
                            {/* Nom masqué sur très petits écrans */}
                            <span className="text-white text-sm font-medium hidden sm:block">
                                {user?.prenom} {user?.nom}
                            </span>
                            <button
                                onClick={handleDeconnexion}
                                className="
                                    text-gray-400 hover:text-white text-sm font-medium
                                    transition-all duration-200 px-3 py-1.5 rounded-lg
                                    hover:bg-white/5 cursor-pointer active:scale-95
                                "
                            >
                                Déconnexion
                            </button>
                        </div>
                    </div>
                </nav>

                {/*Contenu scrollable
                    pb-24 : espace en bas pour ne pas être masqué par le bouton fixe ── */}
                <main className="flex-1 overflow-y-auto max-w-5xl w-full mx-auto px-6 py-8 pb-24">

                    {/* En-tête : titre + contexte (offre unique ou toutes) */}
                    <div className="mb-6">
                        <h1 className="text-white font-black text-2xl">Réservations reçues</h1>
                        {offre ? (
                            // Mode offre unique : affiche le titre et le lieu de l'offre ciblée
                            <p className="text-gray-400 text-sm mt-1">
                                pour <span className="text-amber-400 font-semibold">{offre.titre}</span>
                                {' · '}{offre.lieu}
                            </p>
                        ) : (
                            // Mode "toutes" : libellé générique
                            <p className="text-gray-400 text-sm mt-1">Toutes vos offres</p>
                        )}
                    </div>

                    {/* Cartes de stats : total / en attente / retirées */}
                    <div className="grid grid-cols-3 gap-3 mb-6">
                        {[
                            { label: 'Total', value: reservations.length },
                            { label: 'En attente', value: nbEnAttente },
                            { label: 'Retirées', value: nbRetirees },
                        ].map(s => (
                            <div
                                key={s.label}
                                className="bg-[#3d281a] border border-white/5 rounded-xl px-4 py-3 text-center transition-all duration-200 hover:border-amber-400/20 hover:scale-[1.02]"
                            >
                                <div className="text-amber-400 font-black text-xl">{s.value}</div>
                                <div className="text-gray-400 text-xs mt-0.5">{s.label}</div>
                            </div>
                        ))}
                    </div>

                    {/* Filtre par offre — visible uniquement en mode "toutes les offres" */}
                    {!id && offres.length > 0 && (
                        <div className="flex gap-2 mb-5 flex-wrap">
                            {/* Bouton "Toutes" : réinitialise le filtre */}
                            <button
                                onClick={() => setFiltreOffre('TOUTES')}
                                className={`text-xs font-semibold px-4 py-1.5 rounded-full transition-all cursor-pointer hover:scale-105 active:scale-95
                                    ${filtreOffre === 'TOUTES'
                                        ? 'bg-amber-400 text-[#1e130c]'
                                        : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white'}`}
                            >
                                Toutes
                            </button>
                            {/* Un bouton par offre de l'offreur */}
                            {offres.map(o => (
                                <button
                                    key={o.id}
                                    onClick={() => setFiltreOffre(o.id)}
                                    className={`text-xs font-semibold px-4 py-1.5 rounded-full transition-all cursor-pointer hover:scale-105 active:scale-95
                                        ${filtreOffre === o.id
                                            ? 'bg-amber-400 text-[#1e130c]'
                                            : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white'}`}
                                >
                                    {o.titre}
                                </button>
                            ))}
                        </div>
                    )}

                    {/* Liste des réservations  */}
                    {reservationsFiltrees.length === 0 ? (
                        // État vide : aucune réservation correspondant au filtre
                        <div className="flex flex-col items-center justify-center py-16 gap-3 text-center">
                            <span className="text-5xl">📭</span>
                            <p className="text-white font-semibold">Aucune réservation</p>
                            <p className="text-gray-500 text-xs">Les étudiants n'ont pas encore réservé.</p>
                        </div>
                    ) : (
                        <div className="flex flex-col gap-3">
                            {reservationsFiltrees.map(res => {
                                const cfg = STATUT_CFG[res.statutReservation];
                                const enAttente = res.statutReservation === 'EN_ATTENTE';

                                return (
                                    <div
                                        key={res.id}
                                        className="bg-[#3d281a] border border-white/5 rounded-2xl p-4 flex flex-col sm:flex-row sm:items-center gap-4 transition-all duration-200 hover:border-amber-400/20"
                                    >
                                        {/* Avatar de l'étudiant : initiales dans un cercle */}
                                        <div className="w-10 h-10 rounded-full bg-white/10 flex items-center justify-center shrink-0">
                                            <span className="text-white font-black text-sm">
                                                {res.etudiantPrenom?.charAt(0)}{res.etudiantNom?.charAt(0)}
                                            </span>
                                        </div>

                                        {/* Informations de la réservation */}
                                        <div className="flex-1 min-w-0">
                                            {/* Nom complet de l'étudiant */}
                                            <p className="text-white font-semibold text-sm">
                                                {res.etudiantPrenom} {res.etudiantNom}
                                            </p>
                                            {/* Date de réservation formatée */}
                                            <p className="text-gray-400 text-xs mt-0.5">
                                                Réservé le {formatDate(res.dateReservation)}
                                            </p>
                                            {/* Titre de l'offre — affiché seulement en mode "toutes" */}
                                            {!id && res.offreTitreLocal && (
                                                <p className="text-amber-400/70 text-xs mt-0.5">
                                                    🏷️ {res.offreTitreLocal}
                                                </p>
                                            )}
                                        </div>

                                        {/* Badge du statut courant (EN_ATTENTE / RETIREE / NON_RETIREE) */}
                                        <span className={`text-xs font-bold px-3 py-1.5 rounded-full shrink-0 ${cfg.badgeClass}`}>
                                            {cfg.label}
                                        </span>

                                        {/* Boutons d'action — visibles uniquement pour les réservations EN_ATTENTE */}
                                        {enAttente && (
                                            <div className="flex gap-2 shrink-0">
                                                {/* Marquer comme retirée : appel API PUT */}
                                                <button
                                                    onClick={() => marquer(res.id, 'retiree')}
                                                    disabled={actionId === res.id} // Désactivé pendant la requête
                                                    className="text-green-400 text-xs font-semibold px-3 py-1.5 rounded-lg bg-green-500/10 hover:bg-green-500/20 transition-colors disabled:opacity-50 cursor-pointer"
                                                >
                                                    ✅ Retirée
                                                </button>
                                                {/* Marquer comme non retirée : appel API PUT */}
                                                <button
                                                    onClick={() => marquer(res.id, 'non-retiree')}
                                                    disabled={actionId === res.id} // Désactivé pendant la requête
                                                    className="text-red-400 text-xs font-semibold px-3 py-1.5 rounded-lg bg-red-500/10 hover:bg-red-500/20 transition-colors disabled:opacity-50 cursor-pointer"
                                                >
                                                    ❌ Non retirée
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </main>
            </div>

            {/* ── Bouton retour rond flottant   */}
            <button
                onClick={() => navigate(id ? `/offres/${id}` : '/dashboard')}
                className="
                    fixed bottom-10 right-10
                    w-14 h-14 rounded-full
                    bg-[#d98236] hover:bg-[#c4712b]
                    text-[#1e130c]
                    flex items-center justify-center
                    shadow-2xl
                    border-4 border-[#1e130c]
                    hover:scale-110 active:scale-95
                    transition-all z-[100]
                    cursor-pointer
                "
                title={id ? 'Retour au détail de l\'offre' : 'Retour au dashboard'}
            >

                <span className="text-xl font-black">←</span>
            </button>

        </div>
    );
};

export default ReservationsPage;
