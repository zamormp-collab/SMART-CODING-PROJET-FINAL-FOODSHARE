/**
 * Repository pour les utilisateurs de l'application. 1 repository par entité.
 * Ce repository est l'équivalent d'une table SQL 'utilisateurs'
 */

package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Annotation pour indiquer que cette interface sert à accéder à la base de données
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    // Lister par rôle
    List<Utilisateur> findByRole(Role role);

    // Recherche par nom ou prénom (insensible à la casse)
    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    List<Utilisateur> rechercherParMotCle(String recherche);
}