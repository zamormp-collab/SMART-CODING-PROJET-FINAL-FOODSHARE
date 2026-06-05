/**
 * DTO de réponse pour une réservation.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Reservation;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private StatutReservation statutReservation;
    private LocalDateTime dateReservation;

    private Long offreId;
    private String offreTitre;
    private String offreLieu;
    private BigDecimal offrePrix;
    private LocalDateTime debutRetrait;
    private LocalDateTime finRetrait;

    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;

    private Long offreurId;
    private String offreurNom;
    private String offreurPrenom;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .statutReservation(reservation.getStatutReservation())
                .dateReservation(reservation.getDateReservation())
                .offreId(reservation.getOffre().getId())
                .offreTitre(reservation.getOffre().getTitre())
                .offreLieu(reservation.getOffre().getLieu())
                .offrePrix(reservation.getOffre().getPrix())
                .debutRetrait(reservation.getOffre().getDebutRetrait())
                .finRetrait(reservation.getOffre().getFinRetrait())
                .etudiantId(reservation.getEtudiant().getId())
                .etudiantNom(reservation.getEtudiant().getNom())
                .etudiantPrenom(reservation.getEtudiant().getPrenom())
                .offreurId(reservation.getOffre().getOffreur().getId())
                .offreurNom(reservation.getOffre().getOffreur().getNom())
                .offreurPrenom(reservation.getOffre().getOffreur().getPrenom())
                .dateCreation(reservation.getDateCreation())
                .dateModification(reservation.getDateModification())
                .build();
    }
}
