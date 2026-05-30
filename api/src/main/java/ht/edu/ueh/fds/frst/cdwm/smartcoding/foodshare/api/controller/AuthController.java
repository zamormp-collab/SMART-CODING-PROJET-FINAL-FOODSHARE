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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification")
@CrossOrigin(origins = "*")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/inscription")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
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