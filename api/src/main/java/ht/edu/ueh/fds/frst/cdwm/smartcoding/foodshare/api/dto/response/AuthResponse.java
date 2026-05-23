/**
 * Classe pour gérer les requêtes de connexion
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Annotation Lombok pour créer des objets proprement construits
public class AuthResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String token;
    private String message;
}