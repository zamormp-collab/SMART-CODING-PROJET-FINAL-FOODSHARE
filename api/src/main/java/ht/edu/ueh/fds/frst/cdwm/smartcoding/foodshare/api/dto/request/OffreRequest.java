/**
 * DTO pour la création et la modification d'une offre alimentaire.
 * Contient les règles de validation des données d'entrée.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffreRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 200, message = "Le titre doit contenir entre 3 et 200 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix ne peut pas être négatif")
    private BigDecimal prix;

    @NotNull(message = "Le début du créneau est obligatoire")
    private LocalDateTime debutRetrait;

    @NotNull(message = "La fin du créneau est obligatoire")
    private LocalDateTime finRetrait;

    @NotBlank(message = "Le lieu est obligatoire")
    @Size(max = 300, message = "Le lieu ne peut pas dépasser 300 caractères")
    private String lieu;

    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;
}
