package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour un utilisateur.
 * Ne retourne jamais le mot de passe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String adresse;
    private String telephone;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    /**
     * Factory method — convertit une entité en DTO.
     * Le mot de passe n'est jamais inclus.
     */
    public static UtilisateurResponse from(Utilisateur utilisateur) {
        return UtilisateurResponse.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .adresse(utilisateur.getAdresse())
                .telephone(utilisateur.getTelephone())
                .dateCreation(utilisateur.getDateCreation())
                .dateModification(utilisateur.getDateModification())
                .build();
    }
}