import React from 'react';
import { Offre } from '../../types/index';
import OffreCard from './OffreCard';

interface OffreListProps {
  offres: Offre[];
  loading: boolean;
  error: string | null;
  onVoirDetail: (id: number) => void;
  onModifier: (id: number) => void;
  onAnnuler: (id: number) => void;
  onRafraichir: () => void;
}

const OffreList: React.FC<OffreListProps> = ({
  offres, loading, error, onVoirDetail, onModifier, onAnnuler, onRafraichir,
}) => {

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-16 gap-3">
        <div className="w-8 h-8 border-2 border-amber-400/30 border-t-amber-400 rounded-full animate-spin" />
        <p className="text-gray-400 text-sm">Chargement de vos offres…</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center py-16 gap-4">
        <span className="text-4xl">⚠️</span>
        <p className="text-red-400 text-sm font-medium">{error}</p>
        <button
          onClick={onRafraichir}
          className="text-xs font-semibold text-amber-400 border border-amber-400/30 px-4 py-2 rounded-lg hover:bg-amber-400/10 transition-colors"
        >
          Réessayer
        </button>
      </div>
    );
  }

  if (offres.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 gap-3 text-center">
        <span className="text-5xl">🥗</span>
        <p className="text-white font-semibold">Aucune offre ici</p>
        <p className="text-gray-500 text-xs">Publiez votre première offre depuis le bouton ci-dessus.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      {offres.map(offre => (
        <OffreCard
          key={offre.id}
          offre={offre}
          onVoirDetail={onVoirDetail}
          onModifier={onModifier}
          onAnnuler={onAnnuler}
        />
      ))}
    </div>
  );
};

export default OffreList;