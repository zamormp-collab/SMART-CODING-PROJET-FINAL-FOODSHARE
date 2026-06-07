import { useState, useEffect, useCallback } from 'react';
import { Offre, CreateOffrePayload } from '../types/index';
import { getMesOffres, createOffre, updateOffre, annulerOffre } from '../services/OffreService';

interface UseOffresReturn {
  offres: Offre[];
  loading: boolean;
  error: string | null;
  ajouterOffre: (data: CreateOffrePayload) => Promise<Offre>;
  modifierOffre: (id: number, data: Partial<CreateOffrePayload>) => Promise<Offre>;
  supprimerOffre: (id: number) => Promise<void>;
  rafraichir: () => void;
}

export function useOffres(): UseOffresReturn {

  const [offres, setOffres] = useState<Offre[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // FIX: getMesOffres() → GET /api/offres/mes-offres
  // Charge uniquement les offres de l'offreur connecté
  const charger = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getMesOffres();
      setOffres(data);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'Impossible de charger vos offres'
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    charger();
  }, [charger]);

  const ajouterOffre = async (data: CreateOffrePayload): Promise<Offre> => {
    const nouvelleOffre = await createOffre(data);
    setOffres(prev => [nouvelleOffre, ...prev]);
    return nouvelleOffre;
  };

  // NOUVEAU : manquait complètement dans l'ancien hook
  const modifierOffre = async (id: number, data: Partial<CreateOffrePayload>): Promise<Offre> => {
    const offreMaj = await updateOffre(id, data);
    setOffres(prev => prev.map(o => (o.id === id ? offreMaj : o)));
    return offreMaj;
  };

  // FIX: met le statut local à ANNULEE sans supprimer la carte de la liste
  const supprimerOffre = async (id: number): Promise<void> => {
    await annulerOffre(id);
    setOffres(prev =>
      prev.map(o => (o.id === id ? { ...o, statutOffre: 'ANNULEE' as const } : o))
    );
  };

  return {
    offres,
    loading,
    error,
    ajouterOffre,
    modifierOffre,
    supprimerOffre,
    rafraichir: charger,
  };
}