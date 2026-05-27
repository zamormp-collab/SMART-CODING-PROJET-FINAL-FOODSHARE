/**
 * Repository pour les offres alimentaires.
 * Fournit les méthodes d'accès à la base de données pour l'entité Offre.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Offre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    /**
     * Retourne les offres d'un offreur donné, triées par date de création décroissante.
     * Utilisé pour l'écran "Mes offres" côté web React.
     */
    List<Offre> findByOffreurIdOrderByDateCreationDesc(Long offreurId);

    /**
     * Retourne les offres disponibles :
     * - statut ACTIVE
     * - quantité restante > 0
     * - créneau de retrait non dépassé
     * Triées par début de créneau croissant.
     * Utilisé pour l'écran principal côté Android.
     */
    @Query("SELECT o FROM Offre o WHERE o.statutOffre = :statutOffre " +
           "AND o.quantiteRestante > 0 " +
           "AND o.finRetrait > :maintenant " +
           "ORDER BY o.debutRetrait ASC")

    List<Offre> findOffresDisponibles(
            @Param("statutOffre") StatutOffre statutOffre,
            @Param("maintenant") LocalDateTime maintenant);

    /**
     * Retourne les offres par statut pour un offreur donné.
     */
    @Query("SELECT o FROM Offre o WHERE o.offreur.id = :offreurId AND o.statutOffre = :statutOffre")
    List<Offre> findByOffreur_IdAndStatutOffre(
            @Param("offreurId") Long offreurId,
            @Param("statutOffre") StatutOffre statutOffre);
}
