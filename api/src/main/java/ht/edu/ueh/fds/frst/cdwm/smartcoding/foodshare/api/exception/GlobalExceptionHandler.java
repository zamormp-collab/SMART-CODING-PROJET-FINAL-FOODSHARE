/**
 * Gestionnaire d'exception pour les exceptions globales de toute l'API.
 * Mis à jour : ajout des handlers StockEpuiseException et AccesRefuseException
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Annotation pour intercepter toutes les exceptions de l'API REST
public class GlobalExceptionHandler {
    // ─── 400 — Validation @Valid échouée
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // Annotation pour intercepter les exceptions de VALIDATION_DTO
    public ResponseEntity<Map<String, Object>>
    handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {

                    String fieldName =
                            ((FieldError) error).getField();

                    String errorMessage =
                            error.getDefaultMessage();

                    errors.put(fieldName, errorMessage);
                });

        Map<String, Object> response =
                new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status",
                HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        response.put("message",
                "Erreur de validation");

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    // 409 = Conflict — Opération impossible sur l'état actuel
    @ExceptionHandler(EmailDejaUtiliseException.class)
    public ResponseEntity<Map<String, Object>>
    handleEmailDejaUtilise(
            EmailDejaUtiliseException ex) {

        Map<String, Object> response =
                new HashMap<>();

        response.put("timestamp",
                LocalDateTime.now());

        response.put("status",
                HttpStatus.CONFLICT.value());

        response.put("message",
                ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // Code HTTP = 401 Unauthorized
    @ExceptionHandler(
            IdentifiantsInvalidesException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleIdentifiantsInvalides(
            IdentifiantsInvalidesException ex) {

        Map<String, Object> response =
                new HashMap<>();

        response.put("timestamp",
                LocalDateTime.now());

        response.put("status",
                HttpStatus.UNAUTHORIZED.value());

        response.put("message",
                ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
}

    // Code HTTP = 400 — Erreur métier (créneau invalide, offre non modifiable…)
    @ExceptionHandler(CreneauInvalideException.class)
    public ResponseEntity<Map<String, Object>> handleCreneauInvalide(
            CreneauInvalideException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    // ─── 403 — Accès refusé (mauvais propriétaire)
    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<Map<String, Object>> handleAccesRefuse(
            AccesRefuseException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ─── 404 — Ressource introuvable
    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> handleRessourceIntrouvable(
            RessourceIntrouvableException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 409 — Opération impossible sur l'état actuel
    @ExceptionHandler(OperationNonAutoriseeException.class)
    public ResponseEntity<Map<String, Object>> handleOperationNonAutorisee(
            OperationNonAutoriseeException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    // 409 — Stock épuisé
    @ExceptionHandler(StockEpuiseException.class)
    public ResponseEntity<Map<String, Object>> handleStockEpuise(
            StockEpuiseException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}