package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private String token;
    private String message;
}