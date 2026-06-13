/**
 * Implémentation du service de gestion des offres alimentaires.
 * Contient toute la logique métier : validation, création, modification, annulation.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.OffreRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.OffreResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Offre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.AccesRefuseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.CreneauInvalideException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.RessourceIntrouvableException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.OffreRepository;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffreService implements IOffreService {

    private final OffreRepository offreRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public OffreResponse creerOffre(OffreRequest request, Utilisateur offreur) {

        // Vérification du rôle — seul un OFFREUR peut créer une offre
        if (offreur.getRole() != Role.OFFREUR) {
            throw new AccesRefuseException("Seul un offreur peut publier une offre");
        }

        // Validation métier : la fin du créneau doit être après le début
        if (!request.getFinRetrait().isAfter(request.getDebutRetrait())) {
            throw new CreneauInvalideException(
                "La fin du créneau doit être postérieure au début");
        }

        // Validation métier : le créneau ne doit pas être dans le passé
        if (request.getDebutRetrait().isBefore(LocalDateTime.now())) {
            throw new CreneauInvalideException(
                "Le début du créneau ne peut pas être dans le passé");
        }

        // Construction de l'entité Offre
        Offre offre = Offre.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .quantiteInitiale(request.getQuantite())
                .quantiteRestante(request.getQuantite())
                .prix(request.getPrix())
                .debutRetrait(request.getDebutRetrait())
                .finRetrait(request.getFinRetrait())
                .lieu(request.getLieu())
                .imageUrl(request.getImageUrl())
                .statutOffre(StatutOffre.ACTIVE)
                .offreur(offreur)
                .build();

        // Sauvegarde et retour du DTO
        return OffreResponse.from(offreRepository.save(offre));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OffreResponse> listerOffresDisponibles() {
        // Retourne les offres ACTIVE, quantite > 0, créneau non dépassé
        return offreRepository
                .findOffresDisponibles(StatutOffre.ACTIVE, LocalDateTime.now())
                .stream()
                .map(OffreResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OffreResponse rechercherOffreParId(Long id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                    "Offre introuvable : id=" + id));
        return OffreResponse.from(offre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OffreResponse> consulterMesOffres(Long offreur) {
        return offreRepository
                .findByOffreurIdOrderByDateCreationDesc(offreur)
                .stream()
                .map(OffreResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public OffreResponse modifierOffre(Long id, OffreRequest request, Utilisateur offreur) {

        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                    "Offre introuvable : id=" + id));

        // Vérification : seul le propriétaire peut modifier son offre
        if (!offre.getOffreur().getId().equals(offreur.getId())) {
            throw new AccesRefuseException(
                "Vous n'êtes pas autorisé à modifier cette offre");
        }

        // Vérification : on ne peut pas modifier une offre annulée ou expirée
        if (offre.getStatutOffre() != StatutOffre.ACTIVE) {
            throw new IllegalArgumentException(
                "Impossible de modifier une offre " + offre.getStatutOffre().name().toLowerCase());
        }

        // Validation du créneau
        if (!request.getFinRetrait().isAfter(request.getDebutRetrait())) {
            throw new CreneauInvalideException(
                "La fin du créneau doit être postérieure au début");
        }

        // Mise à jour des champs
        offre.setTitre(request.getTitre());
        offre.setDescription(request.getDescription());
        offre.setPrix(request.getPrix());
        offre.setDebutRetrait(request.getDebutRetrait());
        offre.setFinRetrait(request.getFinRetrait());
        offre.setLieu(request.getLieu());
        offre.setImageUrl(request.getImageUrl());

        return OffreResponse.from(offreRepository.save(offre));
    }

    @Override
    @Transactional
    public void annulerOffre(Long id, Utilisateur offreur) {

        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                    "Offre introuvable : id=" + id));

        // Vérification : seul le propriétaire peut annuler son offre
        if (!offre.getOffreur().getId().equals(offreur.getId())) {
            throw new AccesRefuseException(
                "Vous n'êtes pas autorisé à annuler cette offre");
        }

        // Passage du statut à ANNULEE
        offre.setStatutOffre(StatutOffre.ANNULEE);
        offreRepository.save(offre);
    }
}
