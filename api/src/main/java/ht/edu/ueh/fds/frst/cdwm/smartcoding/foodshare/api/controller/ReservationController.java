/**
 * Controller REST pour la gestion des réservations.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.controller;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.response.ReservationResponse;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service.IReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "API de gestion des réservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final IReservationService reservationService;

    /**
     * POST /api/reservations/offres/{offreId}
     * Réserver une portion d'une offre disponible.
     */
    @PostMapping("/offres/{offreId}")
    @Operation(summary = "Réserver une portion d'une offre")
    public ResponseEntity<ReservationResponse> reserverOffre(
            @PathVariable Long offreId,
            @AuthenticationPrincipal Utilisateur etudiant) {
        ReservationResponse response = reservationService.reserverOffre(offreId, etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/reservations/mes-reservations
     * Consulter les réservations de l'étudiant connecté.
     */
    @GetMapping("/mes-reservations")
    @Operation(summary = "Lister mes réservations")
    public ResponseEntity<List<ReservationResponse>> consulterMesReservations(
            @AuthenticationPrincipal Utilisateur etudiant) {
        return ResponseEntity.ok(reservationService.consulterMesReservations(etudiant.getId()));
    }

    /**
     * GET /api/reservations/offres/{offreId}
     * Consulter les réservations reçues pour une offre.
     */
    @GetMapping("/offres/{offreId}")
    @Operation(summary = "Lister les réservations reçues pour une offre")
    public ResponseEntity<List<ReservationResponse>> consulterReservationsOffre(
            @PathVariable Long offreId,
            @AuthenticationPrincipal Utilisateur offreur) {
        return ResponseEntity.ok(reservationService.consulterReservationsOffre(offreId, offreur));
    }

    /**
     * PATCH /api/reservations/{id}/retiree
     * Marquer une réservation comme retirée.
     */
    @PatchMapping("/{id}/retiree")
    @Operation(summary = "Marquer une réservation comme retirée")
    public ResponseEntity<ReservationResponse> marquerCommeRetiree(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur offreur) {
        return ResponseEntity.ok(reservationService.marquerCommeRetiree(id, offreur));
    }

    /**
     * PATCH /api/reservations/{id}/non-retiree
     * Marquer une réservation comme non retirée.
     */
    @PatchMapping("/{id}/non-retiree")
    @Operation(summary = "Marquer une réservation comme non retirée")
    public ResponseEntity<ReservationResponse> marquerCommeNonRetiree(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur offreur) {
        return ResponseEntity.ok(reservationService.marquerCommeNonRetiree(id, offreur));
    }
}
