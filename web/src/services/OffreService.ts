import { apiFetch } from './api';
import { Offre, CreateOffrePayload } from '../types/index';

// GET /api/offres — toutes les offres disponibles
export async function getOffres(): Promise<Offre[]> {
  return await apiFetch<Offre[]>('/offres');
}

// GET /api/offres/mes-offres — offres de l'offreur connecté uniquement
export async function getMesOffres(): Promise<Offre[]> {
  return await apiFetch<Offre[]>('/offres/mes-offres');
}

// GET /api/offres/{id}
export async function getOffreById(id: number): Promise<Offre> {
  return await apiFetch<Offre>(`/offres/${id}`);
}

// POST /api/offres
export async function createOffre(data: CreateOffrePayload): Promise<Offre> {
  return await apiFetch<Offre>('/offres', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

// PUT /api/offres/{id}
export async function updateOffre(id: number, data: Partial<CreateOffrePayload>): Promise<Offre> {
  return await apiFetch<Offre>(`/offres/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

// DELETE /api/offres/{id} — passe le statut à ANNULEE, retourne 204
export async function annulerOffre(id: number): Promise<void> {
  return await apiFetch<void>(`/offres/${id}`, {
    method: 'DELETE',
  });
}