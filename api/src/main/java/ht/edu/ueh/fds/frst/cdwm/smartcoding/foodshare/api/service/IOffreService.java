/**
 * Interface de service pour la gestion des offres alimentaires.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.OffreRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.OffreResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;

import java.util.List;

public interface IOffreService {

    /**
     * Créer une nouvelle offre — réservé aux OFFREUR
     */
    OffreResponse creerOffre(OffreRequest request, Utilisateur offreur);

    /**
     * Récupérer toutes les offres disponibles du jour — accessible aux ETUDIANT
     */
    List<OffreResponse> listerOffresDisponibles();

    /**
     * Récupérer le détail d'une offre par son id
     */
    OffreResponse rechercherOffreParId(Long id);

    /**
     * Récupérer les offres d'un offreur connecté
     */
    List<OffreResponse> consulterMesOffres(Long offreurId);

    /**
     * Modifier une offre existante — réservé à l'offreur propriétaire
     */
    OffreResponse modifierOffre(Long id, OffreRequest request, Utilisateur offreur);

    /**
     * Annuler une offre — réservé à l'offreur propriétaire
     */
    void annulerOffre(Long id, Utilisateur offreur);
}
