/**
 * Entité JPA représentant une offre alimentaire publiée par un offreur.
 * Une offre contient les informations sur la nourriture disponible,
 * le créneau de retrait, le lieu et la quantité disponible.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre court et descriptif de l'offre (ex: "Sandwich jambon-beurre")
     */
    @Column(nullable = false)
    private String titre;

    /**
     * Description détaillée de l'offre
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Quantité initiale disponible — immuable après création
     */
    @Column(nullable = false)
    private Integer quantiteInitiale;

    /**
     * Quantité restante — décrémentée à chaque réservation
     */
    @Column(nullable = false)
    private Integer quantiteRestante;

    /**
     * Prix en gourdes — 0 signifie don gratuit
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    /**
     * Début du créneau de retrait
     */
    @Column(nullable = false)
    private LocalDateTime debutRetrait;

    /**
     * Fin du créneau de retrait
     */
    @Column(nullable = false)
    private LocalDateTime finRetrait;

    /**
     * Lieu de retrait de l'offre
     */
    @Column(nullable = false)
    private String lieu;

    /**
     * Statut de l'offre : ACTIVE, EXPIREE, ANNULEE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutOffre statutOffre = StatutOffre.ACTIVE;

    /**
     * URL ou chemin de l'image de l'offre.
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * Relation vers l'offreur propriétaire de l'offre
     * FetchType.LAZY = l'offreur n'est chargé que si on y accède
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offreur_Id", nullable = false)
    private Utilisateur offreur;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dateModification;
}
