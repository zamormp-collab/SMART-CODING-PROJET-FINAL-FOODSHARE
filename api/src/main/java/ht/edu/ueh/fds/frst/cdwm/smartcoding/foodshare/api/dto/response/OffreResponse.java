/**
 * DTO de réponse pour une offre alimentaire.
 * Ne retourne jamais l'entité directement — évite d'exposer
 * des données sensibles ou des relations JPA non souhaitées.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Offre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
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
public class OffreResponse {

    private Long id;
    private String titre;
    private String description;
    private Integer quantiteInitiale;
    private Integer quantiteRestante;
    private BigDecimal prix;
    private LocalDateTime debutRetrait;
    private LocalDateTime finRetrait;
    private String lieu;
    private StatutOffre statutOffre;

    // Informations de l'offreur (sans mot de passe ni données sensibles)
    private Long offreurId;
    private String offreurNom;
    private String offreurPrenom;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    /**
     * Méthode usine qui convertit une entité Offre en OffreResponse.
     * Centralise la logique de mapping en un seul endroit.
     */
    public static OffreResponse from(Offre offre) {
        return OffreResponse.builder()
                .id(offre.getId())
                .titre(offre.getTitre())
                .description(offre.getDescription())
                .quantiteInitiale(offre.getQuantiteInitiale())
                .quantiteRestante(offre.getQuantiteRestante())
                .prix(offre.getPrix())
                .debutRetrait(offre.getDebutRetrait())
                .finRetrait(offre.getFinRetrait())
                .lieu(offre.getLieu())
                .statutOffre(offre.getStatutOffre())
                .offreurId(offre.getOffreur().getId())
                .offreurNom(offre.getOffreur().getNom())
                .offreurPrenom(offre.getOffreur().getPrenom())
                .dateCreation(offre.getDateCreation())
                .dateModification(offre.getDateModification())
                .build();
    }
}
