package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.LoginRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.RegisterRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.AuthResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Tests unitaires")
class AuthServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ─────────────────────────────────────────────
    //  REGISTER
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("register() crée un utilisateur et retourne un token valide")
    void register_doit_creer_utilisateur_et_retourner_token() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@uh.ht");
        request.setMotDePasse("password123");
        request.setRole(Role.OFFREUR);
        request.setAdresse("Pétion-Ville");
        request.setTelephone("509-1234-5678");

        when(utilisateurRepository.existsByEmail("jean@uh.ht"))
                .thenReturn(false);
        when(passwordEncoder.encode("password123"))
                .thenReturn("$2a$10$hashed");
        when(utilisateurRepository.save(any(Utilisateur.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("jean@uh.ht", "OFFREUR"))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("jean@uh.ht");
        assertThat(response.getNom()).isEqualTo("Dupont");
        assertThat(response.getMessage()).isEqualTo("Inscription réussie");

        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("register() rejette si l'email est déjà utilisé")
    void register_doit_rejeter_email_existant() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existe@uh.ht");
        request.setMotDePasse("pass123");

        when(utilisateurRepository.existsByEmail("existe@uh.ht"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("email existe déjà");

        verify(utilisateurRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("login() retourne un token si les identifiants sont corrects")
    void login_doit_retourner_token_si_identifiants_corrects() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("jean@uh.ht");
        request.setMotDePasse("password123");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setEmail("jean@uh.ht");
        utilisateur.setMotDePasse("$2a$10$hashed");
        utilisateur.setRole(Role.OFFREUR);

        when(utilisateurRepository.findByEmail("jean@uh.ht"))
                .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", "$2a$10$hashed"))
                .thenReturn(true);
        when(jwtService.generateToken("jean@uh.ht", "OFFREUR"))
                .thenReturn("valid-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("valid-token");
        assertThat(response.getEmail()).isEqualTo("jean@uh.ht");
        assertThat(response.getMessage()).isEqualTo("Connexion réussie");
    }

    @Test
    @DisplayName("login() rejette si l'email est introuvable")
    void login_doit_rejeter_email_inexistant() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("inconnu@uh.ht");
        request.setMotDePasse("password");

        when(utilisateurRepository.findByEmail("inconnu@uh.ht"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email ou mot de passe incorrect");
    }

    @Test
    @DisplayName("login() rejette si le mot de passe est incorrect")
    void login_doit_rejeter_mauvais_mot_de_passe() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("jean@uh.ht");
        request.setMotDePasse("wrongpass");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("jean@uh.ht");
        utilisateur.setMotDePasse("$2a$10$hashed");

        when(utilisateurRepository.findByEmail("jean@uh.ht"))
                .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashed"))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email ou mot de passe incorrect");
    }

    // ─────────────────────────────────────────────
    //  EMAIL EXISTS
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("emailExists() retourne true si l'email existe")
    void emailExists_doit_retourner_true_si_email_existe() {
        // Arrange
        when(utilisateurRepository.existsByEmail("jean@uh.ht"))
                .thenReturn(true);

        // Act
        boolean result = authService.emailExists("jean@uh.ht");

        // Assert
        assertThat(result).isTrue();
    }
}