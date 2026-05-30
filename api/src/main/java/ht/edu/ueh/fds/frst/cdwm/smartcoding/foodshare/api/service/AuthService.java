package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.LoginRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.RegisterRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.AuthResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.security.JwtService;

//import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }

        // Créer le nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole(request.getRole());
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setTelephone(request.getTelephone());

        // Sauvegarder
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Token Jwt
        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        // Retourner la réponse
        return AuthResponse.builder()
                //.id(savedUser.getId())
                .nom(savedUser.getNom())
                .prenom(savedUser.getPrenom())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .token(token)
                .message("Inscription réussie")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Rechercher l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // Token JWT
        String token = jwtService.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().name()
        );

        // Retourner la réponse
        return AuthResponse.builder()
                //.id(utilisateur.getId())
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