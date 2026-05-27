/**
 * Controller REST pour la gestion des offres alimentaires.
 * Expose les endpoints de création, lecture et modification des offres.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.controller;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.OffreRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.OffreResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service.IOffreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
@Tag(name = "Offres", description = "API de gestion des offres alimentaires")
@SecurityRequirement(name = "bearerAuth") // Indique à Swagger que ces endpoints nécessitent un token JWT
public class OffreController {

    private final IOffreService offreService;

    /**
     * GET /api/offres
     * Retourne toutes les offres disponibles du jour.
     * Accessible aux OFFREUR et ETUDIANT.
     */
    @GetMapping
    @Operation(summary = "Lister les offres disponibles du jour")
    public ResponseEntity<List<OffreResponse>> listerOffresDisponibles() {
        return ResponseEntity.ok(offreService.listerOffresDisponibles());
    }

    /**
     * GET /api/offres/{id}
     * Retourne le détail d'une offre.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir le détail d'une offre")
    public ResponseEntity<OffreResponse> rechercherOffreParId(@PathVariable Long id) {
        return ResponseEntity.ok(offreService.rechercherOffreParId(id));
    }

    /**
     * GET /api/offres/mes-offres
     * Retourne les offres de l'offreur connecté.
     * @AuthenticationPrincipal injecte l'utilisateur extrait du token JWT
     */
    @GetMapping("/mes-offres")
    @Operation(summary = "Lister mes offres (offreur connecté)")
    public ResponseEntity<List<OffreResponse>> consulterMesOffres(
            @AuthenticationPrincipal Utilisateur offreur) {
        return ResponseEntity.ok(offreService.consulterMesOffres(offreur.getId()));
    }

    /**
     * POST /api/offres
     * Créer une nouvelle offre.
     * Réservé aux utilisateurs avec le rôle OFFREUR.
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle offre d'invendus alimentaires")
    public ResponseEntity<OffreResponse> creerOffre(
            @Valid @RequestBody OffreRequest request,
            @AuthenticationPrincipal Utilisateur offreur) {
        OffreResponse response = offreService.creerOffre(request, offreur);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/offres/{id}
     * Modifier une offre existante.
     * Réservé à l'offreur propriétaire de l'offre.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Modifier une offre existante")
    public ResponseEntity<OffreResponse> modifierOffre(
            @PathVariable Long id,
            @Valid @RequestBody OffreRequest request,
            @AuthenticationPrincipal Utilisateur offreur) {
        return ResponseEntity.ok(offreService.modifierOffre(id, request, offreur));
    }

    /**
     * DELETE /api/offres/{id}
     * Annuler une offre (statut → ANNULEE).
     * Réservé à l'offreur propriétaire de l'offre.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler une offre")
    public ResponseEntity<Void> annulerOffre(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur offreur) {
        offreService.annulerOffre(id, offreur);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
