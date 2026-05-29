/**
 * Implémentation du service de gestion des réservations.
 * Contient la logique métier : contrôle des rôles, stock, propriété de l'offre et statut.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.ReservationResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Offre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Reservation;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutReservation;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.AccesRefuseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.OperationNonAutoriseeException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.RessourceIntrouvableException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.StockEpuiseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.OffreRepository;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final OffreRepository offreRepository;

    @Override
    @Transactional
    public ReservationResponse reserverOffre(Long offreId, Utilisateur etudiant) {

        if (etudiant.getRole() != Role.ETUDIANT) {
            throw new AccesRefuseException("Seul un étudiant peut réserver une offre");
        }

        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Offre introuvable : id=" + offreId));

        if (offre.getStatutOffre() != StatutOffre.ACTIVE) {
            throw new OperationNonAutoriseeException(
                    "Impossible de réserver une offre " + offre.getStatutOffre().name().toLowerCase());
        }

        if (offre.getFinRetrait().isBefore(LocalDateTime.now())) {
            throw new OperationNonAutoriseeException("Impossible de réserver une offre expirée");
        }

        if (offre.getQuantiteRestante() <= 0) {
            throw new StockEpuiseException("Cette offre n'a plus de portion disponible");
        }

        if (reservationRepository.existsByOffreIdAndEtudiantId(offreId, etudiant.getId())) {
            throw new OperationNonAutoriseeException("Vous avez déjà réservé cette offre");
        }

        offre.setQuantiteRestante(offre.getQuantiteRestante() - 1);

        Reservation reservation = Reservation.builder()
                .offre(offre)
                .etudiant(etudiant)
                .dateReservation(LocalDateTime.now())
                .statutReservation(StatutReservation.EN_ATTENTE)
                .build();

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> consulterMesReservations(Long etudiantId) {
        return reservationRepository
                .findByEtudiantIdOrderByDateReservationDesc(etudiantId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> consulterReservationsOffre(Long offreId, Utilisateur offreur) {

        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Offre introuvable : id=" + offreId));

        verifierProprietaire(offre, offreur);

        return reservationRepository
                .findByOffreIdOrderByDateReservationDesc(offreId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ReservationResponse marquerCommeRetiree(Long reservationId, Utilisateur offreur) {
        Reservation reservation = rechercherReservationAutorisee(reservationId, offreur);
        reservation.setStatutReservation(StatutReservation.RETIREE);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse marquerCommeNonRetiree(Long reservationId, Utilisateur offreur) {
        Reservation reservation = rechercherReservationAutorisee(reservationId, offreur);
        reservation.setStatutReservation(StatutReservation.NON_RETIREE);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private Reservation rechercherReservationAutorisee(Long reservationId, Utilisateur offreur) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Réservation introuvable : id=" + reservationId));

        verifierProprietaire(reservation.getOffre(), offreur);

        return reservation;
    }

    private void verifierProprietaire(Offre offre, Utilisateur offreur) {
        if (offreur.getRole() != Role.OFFREUR) {
            throw new AccesRefuseException("Seul un offreur peut gérer les réservations reçues");
        }

        if (!offre.getOffreur().getId().equals(offreur.getId())) {
            throw new AccesRefuseException(
                    "Vous n'êtes pas autorisé à gérer les réservations de cette offre");
        }
    }
}
