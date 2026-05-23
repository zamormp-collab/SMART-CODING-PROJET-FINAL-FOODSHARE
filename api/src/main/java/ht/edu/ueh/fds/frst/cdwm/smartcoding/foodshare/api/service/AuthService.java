/**
 * Implémentation du service AuthService.java pour l'authentification et l'inscription des utilisateurs.
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.LoginRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.RegisterRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.AuthResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.EmailDejaUtiliseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.IdentifiantsInvalidesException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.security.JwtService;


@Service // Annotation pour indiquer que c'est un service'
@RequiredArgsConstructor // Annotation lombok pour générer automatiquement un constructeur avec les dépendances obligatoires
public class AuthService implements IAuthService {

    // Injection de Dépendances Spring Boot (UtilisateurRepository, PasswordEncoder, JwtService)
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override // Notation pour indiquer
    @Transactional // Notation pour garantir la transaction
    public AuthResponse register(RegisterRequest request) {
        // Vérification de l'existence de l'email entré par l'utilisateur dans la base de données
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new EmailDejaUtiliseException("Un compte avec cet email existe déjà");
        }

        // Création d'un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail().toLowerCase());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole(request.getRole());
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setTelephone(request.getTelephone());

        // Sauvegarde de l'utilisateur
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Génération de Token Jwt pour l'utilisateur
        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        // Retour de la réponse d'inscription avec le token JWT
        return AuthResponse.builder()
                .id(savedUser.getId())
                .nom(savedUser.getNom())
                .prenom(savedUser.getPrenom())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .token(token)
                .message("Inscription réussie")
                .build();
    }

    @Override // Annotation pour indiquer que cette méthode redéfinit une méthode héritée d’une classe parent ou d’une interface (IAuthService dans ce cas)
    public AuthResponse login(LoginRequest request) {
        // Rechercher l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IdentifiantsInvalidesException("Email ou mot de passe incorrect"));

        // Vérifier le mot de passe entré par l'utilisateur
        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        // Génération de Token Jwt pour l'utilisateur
        String token = jwtService.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().name()
        );

        // Retourner la réponse de connexion avec le token JWT
        return AuthResponse.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .token(token)
                .message("Connexion réussie")
                .build();
    }

    @Override
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}