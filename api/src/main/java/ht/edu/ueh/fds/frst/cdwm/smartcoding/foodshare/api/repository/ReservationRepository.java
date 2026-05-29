/**
 * Repository pour les réservations.
 */
package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByOffreIdAndEtudiantId(Long offreId, Long etudiantId);

    List<Reservation> findByEtudiantIdOrderByDateReservationDesc(Long etudiantId);

    List<Reservation> findByOffreIdOrderByDateReservationDesc(Long offreId);
}
