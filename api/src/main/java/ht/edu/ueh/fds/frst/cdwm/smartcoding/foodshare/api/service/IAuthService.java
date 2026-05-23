/**
 * Interface de service pour gérer les opérations relatives à l'authentification des utilisateurs
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.LoginRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.RegisterRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.AuthResponse;

public interface IAuthService {

    /**
     * Inscription d'un nouvel utilisateur
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Connexion d'un utilisateur
     */
    AuthResponse login(LoginRequest request);

    /**
     * Vérification si un email existe déjà
     */
    boolean emailExists(String email);
}