package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.controller;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.LoginRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.RegisterRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.AuthResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Annotation pour créér une API REST avec Spring MVC
@RequestMapping("/api/auth") // Annotation pour définir la route de base commune à toutes les méthodes de cette classe
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification") // Annotation pour ajouter des informations sur le tag de cette classe

public class AuthController {

    private final IAuthService authService;

    @PostMapping("/inscription") // Annotation pour créér ou enregistrer l'inscription d'un utilisateur
    @Operation(summary = "Inscription d'un nouvel utilisateur") // Annotation pour ajouter une description de l'API dans Swagger
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) { // @Valid pour vérifier que la donnée d'inscription n'est pas corrompue ou incomplète (contrôle de sécurité), @RequestBody pour réceptionner la donnée
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/connexion")
    @Operation(summary = "Connexion d'un utilisateur")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verification-email")
    @Operation(summary = "Vérifier si un email existe")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}