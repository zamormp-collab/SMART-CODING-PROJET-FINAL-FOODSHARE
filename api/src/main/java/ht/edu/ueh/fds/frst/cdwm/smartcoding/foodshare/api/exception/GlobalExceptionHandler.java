/**
 * Gestionnaire d'exception pour les exceptions globales de toute l'API.
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

    @ExceptionHandler(MethodArgumentNotValidException.class) // Annotation pour intercepter les exceptions de VALIDATION_DTO
    public ResponseEntity<Map<String, Object>>
    handleValidationExceptions(
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


    @ExceptionHandler( // Annotation pour intercepter les exceptions d'exécution d'EMAIL_DEJA_UTILISE
            EmailDejaUtiliseException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleEmailDejaUtiliseException(
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

    @ExceptionHandler( // Annotation pour intercepter les exceptions de validation d'IDENTIFIANTS_INVALIDES
            IdentifiantsInvalidesException.class
    )
    public ResponseEntity<Map<String, Object>>
    handleIdentifiantsInvalidesException(
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

