package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la modification d'un utilisateur.
 * Tous les champs sont optionnels — on ne met à jour que ce qui est fourni.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurUpdateRequest {

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String prenom;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String adresse;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String telephone;
}