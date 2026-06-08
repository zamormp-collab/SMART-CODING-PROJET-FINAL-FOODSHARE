import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useOffres } from '../hooks/useOffres';
import { useAuth } from '../Context/AuthContext';
import OffreList from '../components/Offres/OffreList';
import { Offre, StatutOffre } from '../types/index';

// Types 
type FiltreStatut = 'TOUTES' | StatutOffre;

//  Options de filtrage affichées au-dessus de la liste 
const FILTRES: { value: FiltreStatut; label: string }[] = [
  { value: 'TOUTES', label: 'Toutes' },
  { value: 'ACTIVE', label: 'Actives' },
  { value: 'EXPIREE', label: 'Expirées' },
  { value: 'ANNULEE', label: 'Annulées' },
];

//  Offres de démonstration 
const OFFRES_MOCK: Offre[] = [
  {
    id: 1,
    titre: 'Croissants du matin',
    imageUrl: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPd_EgccrEalu9AR9YHkNmL4o3Uk4Smmx4pw&s.png',
    description: 'Viennoiseries fraîches invendues',
    lieu: 'Boulangerie centrale',
    prix: 0,
    quantiteInitiale: 10,
    quantiteRestante: 8,
    debutRetrait: '2025-06-02T07:00:00',
    finRetrait: '2025-06-02T10:00:00',
    statutOffre: 'ACTIVE',
    offreurId: 0,
  },
  {
    id: 2,
    titre: 'Soupe de légumes',
    imageUrl: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSVWP_mO8KA-_TSqotj3vbsMg0JwbIrnQtNsg&spng',
    description: 'Soupe maison, 1L par portion',
    lieu: 'Cantine campus B',
    prix: 10.00,
    quantiteInitiale: 8,
    quantiteRestante: 5,
    debutRetrait: '2025-06-03T12:00:00',
    finRetrait: '2025-06-03T14:00:00',
    statutOffre: 'ACTIVE',
    offreurId: 0,
  },
  {
    id: 3,
    titre: 'Pizza margherita',
    imageUrl: 'https://cloudykitchen.com/wp-content/uploads/2025/12/close-up-margherita-pizza.jpg',
    description: 'Pizzas entières à partager',
    lieu: 'Restaurant universitaire',
    prix: 12.99,
    quantiteInitiale: 5,
    quantiteRestante: 3,
    debutRetrait: '2025-06-02T19:00:00',
    finRetrait: '2025-06-02T21:00:00',
    statutOffre: 'ACTIVE',
    offreurId: 0,
  },
  {
    id: 4,
    titre: 'Salade César',
    imageUrl: 'https://img.magnific.com/free-photo/chicken-caesar-salad_1147-401.jpg?semt=ais_hybrid&w=740&q=80.png',
    description: 'Salade composée, portions individuelles',
    lieu: 'Cafétéria Nord',
    prix: 7.55,
    quantiteInitiale: 8,
    quantiteRestante: 6,
    debutRetrait: '2025-06-03T11:30:00',
    finRetrait: '2025-06-03T13:30:00',
    statutOffre: 'ACTIVE',
    offreurId: 0,
  },
  {
    id: 5,
    titre: 'Brownies chocolat',
    imageUrl: 'https://png.pngtree.com/png-clipart/20231016/original/pngtree-chocolate-brownie-png-png-image_13321549.png',
    description: 'Gâteaux faits maison',
    lieu: 'Foyer étudiant',
    prix: 0,
    quantiteInitiale: 15,
    quantiteRestante: 12,
    debutRetrait: '2025-06-04T15:00:00',
    finRetrait: '2025-06-04T18:00:00',
    statutOffre: 'ACTIVE',
    offreurId: 0,
  },
  {
    id: 6,
    titre: 'Sandwich jambon',
    imageUrl: 'https://boletindesalud.com.ar/wp-content/uploads/2022/01/Recetas-de-sandwiches-ricos-y-saludables.jpg',
    description: 'Sandwiches baguette du midi',
    lieu: 'Snack Porte Est',
    prix: 15.80,
    quantiteInitiale: 6,
    quantiteRestante: 4,
    debutRetrait: '2025-06-02T12:00:00',
    finRetrait: '2025-06-02T14:00:00',
    statutOffre: 'ACTIVE',
    offreurId: 0,
  },
  {
    id: 7,
    titre: 'Quiche lorraine',
    imageUrl: 'https://static.wixstatic.com/media/aed6e4_01293c90fc774b1ca77392df7caba587~mv2.png/v1/fill/w_568,h_710,al_c,q_90,usm_0.66_1.00_0.01,enc_avif,quality_auto/aed6e4_01293c90fc774b1ca77392df7caba587~mv2.png',
    description: 'Quiche traditionnelle, 6 parts',
    lieu: 'Restaurant Sud',
    prix: 7.60,
    quantiteInitiale: 6,
    quantiteRestante: 6,
    debutRetrait: '2025-06-01T12:00:00',
    finRetrait: '2025-06-01T14:00:00',
    statutOffre: 'EXPIREE',
    offreurId: 0,
  },
  {
    id: 8,
    titre: 'Tarte aux pommes',
    imageUrl: 'https://lesrecettesdetiti.fr/wp-content/uploads/2022/03/001-5-1000x500.png',
    description: 'Tarte maison entière',
    lieu: 'Pâtisserie campus',
    prix: 0,
    quantiteInitiale: 3,
    quantiteRestante: 3,
    debutRetrait: '2025-05-31T16:00:00',
    finRetrait: '2025-05-31T19:00:00',
    statutOffre: 'EXPIREE',
    offreurId: 0,
  },
];

//  Items du menu latéral 
const NAV_ITEMS = [
  { label: 'Offres', icon: '🏷️', path: '/dashboard' },
  { label: 'Réservations', icon: '📅', path: '/reservations' },
];

//  Config des cartes de statistiques─
type StatCard = {
  label: string;
  filtre: FiltreStatut;
  getValue: (offres: Offre[]) => number;
};

const STAT_CARDS: StatCard[] = [
  { label: 'Total offres', filtre: 'TOUTES', getValue: o => o.length },
  { label: 'Actives', filtre: 'ACTIVE', getValue: o => o.filter(x => x.statutOffre === 'ACTIVE').length },
  { label: 'Expirées', filtre: 'EXPIREE', getValue: o => o.filter(x => x.statutOffre === 'EXPIREE').length },
  { label: 'Portions dispo', filtre: 'TOUTES', getValue: o => o.reduce((acc, x) => acc + x.quantiteRestante, 0) },
];

// Composant principal 
const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logoutUser } = useAuth();

  const { offres: offresBackend, loading, error, supprimerOffre, rafraichir } = useOffres();

  const [filtre, setFiltre] = useState<FiltreStatut>('TOUTES');
  const [search, setSearch] = useState('');

  // Backend disponible → vraies données. Sinon → mocks visuels.
  const offres: Offre[] = offresBackend.length > 0 ? offresBackend : OFFRES_MOCK;

  const offresFiltrees = offres
    .filter(o => filtre === 'TOUTES' || o.statutOffre === filtre)
    .filter(o =>
      search === '' ||
      o.titre.toLowerCase().includes(search.toLowerCase()) ||
      (o.description ?? '').toLowerCase().includes(search.toLowerCase())
    );

  const handleVoirDetail = (id: number) => {
    const offre = offres.find(o => o.id === id);
    navigate(`/offres/${id}`, { state: { offre } });
  };

  const handleModifier = (id: number) => navigate(`/offres/${id}/modifier`);

  const handleAnnuler = async (id: number) => {
    try { await supprimerOffre(id); }
    catch { alert("Impossible d'annuler cette offre."); }
  };

  const handleDeconnexion = () => { logoutUser(); navigate('/login'); };

  const initiales = user
    ? `${user.prenom.charAt(0)}${user.nom.charAt(0)}`.toUpperCase()
    : '?';

  const isActive = (path: string) => {
    if (path === '/dashboard') return location.pathname === '/dashboard';
    return location.pathname.startsWith(path);
  };

  return (
    <div className="flex min-h-screen bg-[#1e130c]">

      {/*  Sidebar  */}
      <aside className="w-56 min-w-[220px] bg-[#150d07] border-r border-white/5 flex flex-col">

        {/* Logo */}
        <div className="px-5 py-5 border-b border-white/5 flex items-center gap-2.5">
          <div className="bg-white rounded-lg p-1.5 flex items-center justify-center shrink-0">
            <img
              src="/logo-foodshare.png"
              alt="logo FoodShare"
              className="h-5 w-auto object-contain"
              onError={e => {
                (e.currentTarget.parentElement as HTMLElement).style.display = 'none';
              }}
            />
          </div>
          <span className="text-white font-black text-base tracking-wider uppercase">
            FoodShare
          </span>
        </div>

        {/* Navigation */}
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

      {/*  Zone principale  */}
      <div className="flex-1 flex flex-col overflow-hidden">

        {/* Navbar sticky */}
        <nav className="sticky top-0 z-10 bg-[#1e130c]/95 backdrop-blur border-b border-white/5 px-6 py-3">
          <div className="flex items-center gap-3">
            <div className="
              flex items-center gap-2 bg-white/5 border border-white/10
              rounded-xl px-4 py-2.5 w-80
              transition-all duration-200
              hover:border-amber-400/40
              focus-within:border-amber-400/60
            ">
              <span className="text-white/40 text-base">🔍</span>
              <input
                type="text"
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Rechercher une offre..."
                className="bg-transparent border-none outline-none text-white text-sm placeholder-white/40 w-full"
              />
              {search && (
                <button
                  onClick={() => setSearch('')}
                  className="text-white/40 hover:text-white text-sm transition-colors cursor-pointer"
                >
                  ✕
                </button>
              )}
            </div>

            <div className="flex items-center gap-3 ml-auto">
              <div className="w-9 h-9 rounded-full bg-amber-400 flex items-center justify-center">
                <span className="text-[#1e130c] font-black text-sm">{initiales}</span>
              </div>
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

        {/* Contenu scrollable */}
        <main className="flex-1 overflow-y-auto max-w-7xl w-full mx-auto px-6 py-8">

          <div className="flex items-start justify-between mb-8 gap-4 flex-wrap">
            <div>
              <h1 className="text-white font-black text-2xl">
                Bonjour, {user?.prenom} 👋
              </h1>
              <p className="text-gray-400 text-sm mt-1">
                Gérez vos invendus publiés sur FoodShare
              </p>
            </div>
            <button
              onClick={() => navigate('/offres/creer')}
              className="
                flex items-center gap-2 bg-amber-400 hover:bg-amber-500
                text-[#1e130c] font-bold text-sm px-5 py-2.5 rounded-xl
                transition-all duration-200 active:scale-95 cursor-pointer hover:scale-105
              "
            >
              + Publier une offre
            </button>
          </div>

          {/*  Statistiques */}
          {!loading && !error && (
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
              {STAT_CARDS.map(stat => {
                const isSelected = filtre === stat.filtre;
                return (
                  <button
                    key={stat.label}
                    onClick={() => setFiltre(stat.filtre)}
                    className={`
                      bg-[#3d281a] border rounded-xl px-4 py-3 text-center
                      transition-all duration-200 hover:scale-[1.02] active:scale-95
                      cursor-pointer w-full
                      ${isSelected
                        ? 'border-amber-400/70 ring-1 ring-amber-400/30'
                        : 'border-white/5 hover:border-amber-400/30'}
                    `}
                  >
                    <div className="text-amber-400 font-black text-xl">
                      {stat.getValue(offres)}
                    </div>
                    <div className="text-gray-400 text-xs mt-0.5">{stat.label}</div>
                  </button>
                );
              })}
            </div>
          )}

          {/*  Filtres par statut */}
          {!loading && !error && offres.length > 0 && (
            <div className="flex gap-2 mb-5 flex-wrap items-center">
              {FILTRES.map(f => (
                <button
                  key={f.value}
                  onClick={() => setFiltre(f.value)}
                  className={`
                    text-xs font-semibold px-4 py-1.5 rounded-full
                    transition-all duration-200 cursor-pointer
                    hover:scale-105 active:scale-95
                    ${filtre === f.value
                      ? 'bg-amber-400 text-[#1e130c]'
                      : 'bg-white/5 text-gray-400 hover:bg-white/10 hover:text-white'}
                  `}
                >
                  {f.label}
                  {f.value !== 'TOUTES' && (
                    <span className="ml-1.5 opacity-70">
                      ({offres.filter(o => o.statutOffre === f.value).length})
                    </span>
                  )}
                </button>
              ))}
              <button
                onClick={rafraichir}
                className="ml-auto text-gray-500 text-xs hover:text-amber-400 transition-all duration-200 cursor-pointer hover:scale-110 active:scale-95"
              >
                🔄 Actualiser
              </button>
            </div>
          )}

          {/*  Liste des offres  */}
          <OffreList
            offres={offresFiltrees}
            loading={loading}
            error={error}
            onVoirDetail={handleVoirDetail}
            onModifier={handleModifier}
            onAnnuler={handleAnnuler}
            onRafraichir={rafraichir}
          />
        </main>
      </div>
    </div>
  );
};

export default DashboardPage;