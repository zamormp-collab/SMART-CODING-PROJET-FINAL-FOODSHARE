/**
 * Entité JPA représentant une réservation faite par un étudiant.
 * Une réservation relie une offre à un étudiant et garde son statut de retrait.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reservations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_offre_etudiant",
                columnNames = {"offre_id", "etudiant_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Offre réservée par l'étudiant.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_id", nullable = false)
    private Offre offre;

    /**
     * Étudiant qui réserve une portion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Utilisateur etudiant;

    /**
     * Date métier de la réservation.
     */
    @Column(nullable = false)
    private LocalDateTime dateReservation;

    /**
     * Statut de retrait de la réservation.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutReservation statutReservation = StatutReservation.EN_ATTENTE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dateModification;
}
