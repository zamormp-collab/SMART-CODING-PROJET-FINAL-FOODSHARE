import { apiFetch } from './api';
import { Reservation } from '../types/index';

// GET /api/reservations/offres/{offreId}
// Réservations reçues pour une offre donnée (vue offreur)
export async function getReservationsParOffre(offreId: number): Promise<Reservation[]> {
    return await apiFetch<Reservation[]>(`/reservations/offres/${offreId}`);
}

// GET /api/reservations/mes-reservations
// Réservations de l'étudiant connecté
export async function getMesReservations(): Promise<Reservation[]> {
    return await apiFetch<Reservation[]>('/reservations/mes-reservations');
}

// PATCH /api/reservations/{id}/retiree
export async function marquerRetiree(id: number): Promise<Reservation> {
    return await apiFetch<Reservation>(`/reservations/${id}/retiree`, {
        method: 'PATCH',
    });
}

// PATCH /api/reservations/{id}/non-retiree
export async function marquerNonRetiree(id: number): Promise<Reservation> {
    return await apiFetch<Reservation>(`/reservations/${id}/non-retiree`, {
        method: 'PATCH',
    });
}