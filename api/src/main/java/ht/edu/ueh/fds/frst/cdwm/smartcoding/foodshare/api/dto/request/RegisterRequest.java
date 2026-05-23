/**
 * Classe pour gérer les requêtes d'inscription des utilisateurs
 * Définition de règle de validation des colonnes de la table utilisateurs
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(min = 2, max = 100, message = "Le prenom doit contenir entre 2 et 100 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", // Regex pour valider un email selon le format
            message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(regexp = "^(?=.*[0-9]).*$", // Regex pour valider un mot de passe selon le contenu
            message = "Le mot de passe doit contenir au moins un chiffre")
    private String motDePasse;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String adresse;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String telephone;
}