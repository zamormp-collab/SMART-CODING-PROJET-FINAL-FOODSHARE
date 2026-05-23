/**
 * Classe pour gérer les requêtes de connexion
 * Définition de règle de validation du formulaire de connexion
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}