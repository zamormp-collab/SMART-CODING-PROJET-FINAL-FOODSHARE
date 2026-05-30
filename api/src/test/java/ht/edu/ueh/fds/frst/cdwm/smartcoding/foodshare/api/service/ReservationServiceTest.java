package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Offre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Reservation;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutReservation;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.AccesRefuseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.OperationNonAutoriseeException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.RessourceIntrouvableException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.StockEpuiseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.OffreRepository;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du ReservationService.
 *
 * Stratégie : tests unitaires rapides avec Mockito (sans @SpringBootTest).
 * Chaque test couvre une règle métier précise du service.
 * Pattern utilisé : AAA (Arrange / Act / Assert)
 *
 * Règles métier couvertes :
 *  - Seul un ETUDIANT peut réserver une offre
 *  - L'offre doit exister, être ACTIVE et non expirée
 *  - Le stock doit être > 0
 *  - Un étudiant ne peut pas réserver deux fois la même offre
 *  - Seul le propriétaire de l'offre peut gérer ses réservations
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService — Tests unitaires")

class ReservationServiceTest {

    // ─────────────────────────────────────────────
    //  MOCKS — dépendances simulées (pas de base de données réelle)
    // ─────────────────────────────────────────────

    @Mock
    private ReservationRepository reservationRepository;
    // Simule l'accès aux réservations en base

    @Mock
    private OffreRepository offreRepository;
    // Simule l'accès aux offres en base

    @InjectMocks
    private ReservationService reservationService;
    // Instance réelle du service, avec les mocks injectés automatiquement

    // ─────────────────────────────────────────────
    //  DONNÉES COMMUNES — réutilisées dans tous les tests
    // ─────────────────────────────────────────────

    private Utilisateur etudiant;  // utilisateur avec le rôle ETUDIANT
    private Utilisateur offreur;   // utilisateur avec le rôle OFFREUR
    private Offre offre;           // offre active avec stock disponible

    /**
     * Initialisation exécutée avant chaque test.
     * On repart d'un état propre à chaque fois.
     */
    @BeforeEach
    void setUp() {
        // Créer un étudiant valide
        etudiant = new Utilisateur();
        etudiant.setId(1L);
        etudiant.setNom("Étudiant");
        etudiant.setEmail("etudiant@uh.ht");
        etudiant.setRole(Role.ETUDIANT);

        // Créer un offreur valide
        offreur = new Utilisateur();
        offreur.setId(2L);
        offreur.setNom("Offreur");
        offreur.setEmail("offreur@uh.ht");
        offreur.setRole(Role.OFFREUR);

        // Créer une offre active avec 5 portions disponibles
        offre = new Offre();
        offre.setId(10L);
        offre.setTitre("Sandwich poulet");
        offre.setQuantiteRestante(5);
        offre.setStatutOffre(StatutOffre.ACTIVE);
        offre.setFinRetrait(LocalDateTime.now().plusHours(2)); // expire dans 2h
        offre.setOffreur(offreur); // appartient à l'offreur créé ci-dessus
    }

    // ─────────────────────────────────────────────
    //  TESTS : reserverOffre()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("reserverOffre() décrémente la quantité et crée la réservation")
    void reserverOffre_doit_decrementer_quantite_et_creer_reservation() {
        // Arrange — l'offre existe, l'étudiant n'a pas encore réservé
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));
        when(reservationRepository.existsByOffreIdAndEtudiantId(10L, 1L))
                .thenReturn(false); // pas de réservation existante
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0)); // retourne l'objet sauvegardé

        // Act — l'étudiant réserve l'offre
        reservationService.reserverOffre(10L, etudiant);

        // Assert — la quantité a bien été décrémentée de 5 à 4
        assert offre.getQuantiteRestante() == 4;
        // Et la réservation a bien été sauvegardée en base
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("reserverOffre() rejette si l'utilisateur n'est pas ETUDIANT")
    void reserverOffre_doit_rejeter_si_pas_etudiant() {
        // Arrange — un offreur tente de réserver
        Utilisateur pasEtudiant = new Utilisateur();
        pasEtudiant.setRole(Role.OFFREUR);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOffre(10L, pasEtudiant))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("Seul un étudiant peut réserver");

        verify(offreRepository, never()).findById(any());
    }

    @Test
    @DisplayName("reserverOffre() rejette si l'offre est introuvable")
    void reserverOffre_doit_rejeter_si_offre_introuvable() {
        // Arrange
        when(offreRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOffre(99L, etudiant))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Offre introuvable");
    }

    @Test
    @DisplayName("reserverOffre() rejette si l'offre n'est pas ACTIVE")
    void reserverOffre_doit_rejeter_si_offre_inactive() {
        // Arrange
        offre.setStatutOffre(StatutOffre.ANNULEE);
        when(offreRepository.findById(10L)).thenReturn(Optional.of(offre));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOffre(10L, etudiant))
                .isInstanceOf(OperationNonAutoriseeException.class)
                .hasMessageContaining("annulee");
    }

    @Test
    @DisplayName("reserverOffre() rejette si l'offre est expirée")
    void reserverOffre_doit_rejeter_si_offre_expiree() {
        // Arrange
        offre.setFinRetrait(LocalDateTime.now().minusHours(1));
        when(offreRepository.findById(10L)).thenReturn(Optional.of(offre));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOffre(10L, etudiant))
                .isInstanceOf(OperationNonAutoriseeException.class)
                .hasMessageContaining("expirée");
    }

    @Test
    @DisplayName("reserverOffre() rejette si le stock est épuisé")
    void reserverOffre_doit_rejeter_si_stock_epuise() {
        // Arrange
        offre.setQuantiteRestante(0);
        when(offreRepository.findById(10L)).thenReturn(Optional.of(offre));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOffre(10L, etudiant))
                .isInstanceOf(StockEpuiseException.class)
                .hasMessageContaining("plus de portion disponible");
    }
    @Test
    @DisplayName("reserverOffre() rejette si l'étudiant a déjà réservé cette offre")
    void reserverOffre_doit_rejeter_si_deja_reserve() {
        // Arrange
        when(offreRepository.findById(10L)).thenReturn(Optional.of(offre));
        when(reservationRepository.existsByOffreIdAndEtudiantId(10L, 1L))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.reserverOffre(10L, etudiant))
                .isInstanceOf(OperationNonAutoriseeException.class)
                .hasMessageContaining("déjà réservé");
    }

    // ─────────────────────────────────────────────
    //  MARQUER COMME RETIREE / NON RETIREE
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("marquerCommeRetiree() change le statut en RETIREE")
    void marquerCommeRetiree_doit_changer_statut() {
        // Arrange — réservation complète avec offre + étudiant + offreur
        Utilisateur etudiantComplet = new Utilisateur();
        etudiantComplet.setId(1L);
        etudiantComplet.setNom("Étudiant");
        etudiantComplet.setPrenom("Jean");
        etudiantComplet.setEmail("etudiant@uh.ht");
        etudiantComplet.setRole(Role.ETUDIANT);

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setOffre(offre);           // offre appartient à offreur (id=2)
        reservation.setEtudiant(etudiantComplet); // ← étudiant complet obligatoire
        reservation.setStatutReservation(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now());

        when(reservationRepository.findById(100L))
                .thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        reservationService.marquerCommeRetiree(100L, offreur);

        // Assert — statut changé à RETIREE
        assert reservation.getStatutReservation() == StatutReservation.RETIREE;
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("marquerCommeRetiree() rejette si l'offreur ne possède pas l'offre")
    void marquerCommeRetiree_doit_rejeter_si_pas_proprietaire() {
        // Arrange
        Utilisateur autreOffreur = new Utilisateur();
        autreOffreur.setId(99L);
        autreOffreur.setRole(Role.OFFREUR);

        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setOffre(offre); // offre appartient à offreur (id=2), pas autreOffreur (id=99)

        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.marquerCommeRetiree(100L, autreOffreur))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("pas autorisé");
    }




}