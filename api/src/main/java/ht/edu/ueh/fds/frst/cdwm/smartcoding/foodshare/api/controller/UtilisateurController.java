package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.controller;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.UtilisateurUpdateRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.UtilisateurResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service.IUtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class UtilisateurController {

    private final IUtilisateurService utilisateurService;

    /**
     * GET /api/utilisateurs
     * Lister tous les utilisateurs.
     */
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<List<UtilisateurResponse>> listerTous() {
        return ResponseEntity.ok(utilisateurService.listerTous());
    }

    /**
     * GET /api/utilisateurs/{id}
     * Rechercher un utilisateur par son id.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un utilisateur par son id")
    public ResponseEntity<UtilisateurResponse> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.trouverParId(id));
    }

    /**
     * GET /api/utilisateurs/recherche?motCle=dupont
     * Rechercher par mot clé (nom, prénom, email).
     */
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher un utilisateur par mot clé")
    public ResponseEntity<List<UtilisateurResponse>> rechercher(
            @RequestParam String motCle) {
        return ResponseEntity.ok(utilisateurService.rechercher(motCle));
    }

    /**
     * PATCH /api/utilisateurs/{id}
     * Modifier un utilisateur — mise à jour partielle.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<UtilisateurResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.modifier(id, request));
    }

    /**
     * DELETE /api/utilisateurs/{id}
     * Supprimer un utilisateur définitivement.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
        return ResponseEntity.noContent().build(); // 204
    }
}