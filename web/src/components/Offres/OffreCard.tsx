import React from 'react';
import { Offre } from '../../types/index';

interface OffreCardProps {
  offre: Offre;
  onVoirDetail: (id: number) => void;
  onModifier: (id: number) => void;
  onAnnuler: (id: number) => void;
}

function formatHeure(iso: string): string {
  return new Date(iso).toLocaleTimeString('fr-FR', {
    hour: '2-digit',
    minute: '2-digit',
  });
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('fr-FR');
}

function pourcentageRestant(initiale: number, restante: number): number {
  if (initiale === 0) return 0;
  return Math.round((restante / initiale) * 100);
}

const STATUT_CONFIG = {
  ACTIVE: { label: '● Active', badgeClass: 'bg-green-500/80 text-white' },
  EXPIREE: { label: '● Expirée', badgeClass: 'bg-gray-700/80 text-gray-300' },
  ANNULEE: { label: '● Annulée', badgeClass: 'bg-red-700/80 text-red-200' },
};

const OffreCard: React.FC<OffreCardProps> = ({ offre, onVoirDetail, onModifier, onAnnuler }) => {

  const statut = STATUT_CONFIG[offre.statutOffre] ?? STATUT_CONFIG.EXPIREE;
  const estActive = offre.statutOffre === 'ACTIVE';
  const pct = pourcentageRestant(offre.quantiteInitiale, offre.quantiteRestante);
  const couleurStock =
    pct > 60 ? 'bg-green-500' :
      pct > 30 ? 'bg-amber-400' :
        'bg-red-500';

  return (
    <div
      className={`
        relative flex flex-col rounded-2xl border transition-all duration-200
        overflow-hidden cursor-pointer group
        ${estActive
          ? 'bg-[#3d281a] border-white/10 hover:border-amber-400/50 hover:shadow-xl hover:-translate-y-1'
          : 'bg-[#2a1e13] border-white/5 opacity-60'
        }
      `}
      onClick={() => onVoirDetail(offre.id)}
    >
      {/* Image */}
      <div className="w-full h-40 overflow-hidden relative">
        {offre.imageUrl ? (
          <img
            src={offre.imageUrl}
            alt={offre.titre}
            className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
            onError={(e) => { e.currentTarget.style.display = 'none'; }}
          />
        ) : (
          <div className="w-full h-full bg-black/30 flex items-center justify-center">
            <span className="text-5xl">🍽️</span>
          </div>
        )}

        {/* Badge statut */}
        <div className="absolute top-2 right-2">
          <span className={`text-[10px] font-bold uppercase px-2 py-1 rounded-full backdrop-blur-sm ${statut.badgeClass}`}>
            {statut.label}
          </span>
        </div>

        {/* Badge prix */}
        <div className="absolute bottom-2 left-2">
          {offre.prix === 0 ? (
            <span className="bg-green-500/90 text-white text-[10px] font-bold px-2 py-1 rounded-full">GRATUIT</span>
          ) : (
            <span className="bg-amber-400/90 text-[#1e130c] text-[10px] font-bold px-2 py-1 rounded-full">
              {Number(offre.prix).toFixed(2)} $
            </span>
          )}
        </div>
      </div>

      {/* Contenu */}
      <div className="flex flex-col gap-2 p-4">
        <h3 className="text-white font-bold text-sm leading-tight line-clamp-1">{offre.titre}</h3>
        <p className="text-gray-400 text-xs">📍 {offre.lieu}</p>

        <div className="text-xs text-gray-400 bg-black/20 rounded-lg px-2 py-1.5">
          🕐 {formatDate(offre.debutRetrait)} · {formatHeure(offre.debutRetrait)} → {formatHeure(offre.finRetrait)}
        </div>

        {/* Barre de stock */}
        <div>
          <div className="flex justify-between text-[10px] text-gray-400 mb-1">
            <span>Stock</span>
            <span>
              <span className="text-white font-semibold">{offre.quantiteRestante}</span>
              /{offre.quantiteInitiale}
            </span>
          </div>
          <div className="h-1.5 bg-white/10 rounded-full overflow-hidden">
            <div
              className={`h-full rounded-full transition-all duration-500 ${couleurStock}`}
              style={{ width: `${pct}%` }}
            />
          </div>
        </div>

        {/* Bouton Voir détail — visible pour toutes les offres */}
        <div
          className="flex pt-1 border-t border-white/5"
          onClick={(e) => e.stopPropagation()}
        >
          <button
            onClick={(e) => { e.stopPropagation(); onVoirDetail(offre.id); }}
            className="
              flex-1 text-amber-400 text-xs font-semibold px-3 py-1.5 rounded-lg
              bg-amber-400/10 hover:bg-amber-400/20 transition-all duration-200
              cursor-pointer hover:scale-[1.02] active:scale-95
            "
          >
            👁️ Voir détail
          </button>
        </div>
      </div>
    </div>
  );
};

export default OffreCard;