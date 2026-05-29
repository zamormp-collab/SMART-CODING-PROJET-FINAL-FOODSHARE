/**
 * Interface de service pour la gestion des réservations.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.ReservationResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;

import java.util.List;

public interface IReservationService {

    /**
     * Réserver une portion d'une offre — réservé aux ETUDIANT.
     */
    ReservationResponse reserverOffre(Long offreId, Utilisateur etudiant);

    /**
     * Lister les réservations de l'étudiant connecté.
     */
    List<ReservationResponse> consulterMesReservations(Long etudiantId);

    /**
     * Lister les réservations reçues pour une offre de l'offreur connecté.
     */
    List<ReservationResponse> consulterReservationsOffre(Long offreId, Utilisateur offreur);

    /**
     * Marquer une réservation comme retirée.
     */
    ReservationResponse marquerCommeRetiree(Long reservationId, Utilisateur offreur);

    /**
     * Marquer une réservation comme non retirée.
     */
    ReservationResponse marquerCommeNonRetiree(Long reservationId, Utilisateur offreur);
}
