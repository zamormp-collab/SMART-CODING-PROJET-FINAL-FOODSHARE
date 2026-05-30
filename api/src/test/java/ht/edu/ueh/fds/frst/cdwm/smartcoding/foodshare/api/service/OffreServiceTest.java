package ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.service;

import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.dto.request.OffreRequest;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Offre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.entity.Utilisateur;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.Role;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.enums.StatutOffre;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.AccesRefuseException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.CreneauInvalideException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.exception.RessourceIntrouvableException;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.OffreRepository;
import ht.edu.ueh.fds.frst.cdwm.smartcoding.foodshare.api.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Tests unitaires du OffreService.
 *
 * Stratégie : tests unitaires rapides avec Mockito (sans @SpringBootTest).
 * Chaque test couvre une règle métier précise du service.
 * Pattern utilisé : AAA (Arrange / Act / Assert)
 *
 * Règles métier couvertes :
 *  - Seul un OFFREUR peut créer une offre
 *  - La fin du créneau doit être après le début
 *  - Le début du créneau ne peut pas être dans le passé
 *  - quantiteRestante = quantiteInitiale à la création
 *  - Seul le propriétaire peut modifier ou annuler son offre
 *  - On ne peut pas modifier une offre non ACTIVE
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OffreService — Tests unitaires")
class OffreServiceTest {

    // ─────────────────────────────────────────────
    //  MOCKS — dépendances simulées
    // ─────────────────────────────────────────────

    @Mock
    private OffreRepository offreRepository;
    // Simule l'accès aux offres en base

    @Mock
    private UtilisateurRepository utilisateurRepository;
    // Simule l'accès aux utilisateurs en base

    @InjectMocks
    private OffreService offreService;
    // Instance réelle du service avec les mocks injectés

    // ─────────────────────────────────────────────
    //  DONNÉES COMMUNES
    // ─────────────────────────────────────────────

    private Utilisateur offreur;       // utilisateur OFFREUR valide
    private Utilisateur autreOffreur;  // un autre offreur (pour tester les accès)
    private OffreRequest request;      // requête de création valide
    private Offre offre;

    /**
     * Initialisation avant chaque test.
     * On repart toujours d'un état propre et cohérent.
     */
    @BeforeEach
    void setUp() {
        // Offreur principal
        offreur = new Utilisateur();
        offreur.setId(1L);
        offreur.setNom("Resto");
        offreur.setPrenom("Campus");
        offreur.setEmail("resto@uh.ht");
        offreur.setRole(Role.OFFREUR);

        // Autre offreur (pour tester les refus d'accès)
        autreOffreur = new Utilisateur();
        autreOffreur.setId(99L);
        autreOffreur.setNom("Autre");
        autreOffreur.setPrenom("Offreur");
        autreOffreur.setEmail("autre@uh.ht");
        autreOffreur.setRole(Role.OFFREUR);

        // Requête de création valide — créneau dans le futur
        request = new OffreRequest();
        request.setTitre("Sandwich poulet");
        request.setDescription("Pain complet, poulet grillé");
        request.setQuantite(10);
        request.setPrix(new BigDecimal("150.00"));
        request.setLieu("Cafétéria centrale");
        request.setDebutRetrait(LocalDateTime.now().plusHours(1));
        request.setFinRetrait(LocalDateTime.now().plusHours(3));

        // Offre ACTIVE déjà existante en base (pour les tests de modification)
        offre = new Offre();
        offre.setId(10L);
        offre.setTitre("Sandwich poulet");
        offre.setDescription("Pain complet, poulet grillé");
        offre.setQuantiteInitiale(10);
        offre.setQuantiteRestante(10);
        offre.setPrix(new BigDecimal("150.00"));
        offre.setLieu("Cafétéria centrale");
        offre.setDebutRetrait(LocalDateTime.now().plusHours(1));
        offre.setFinRetrait(LocalDateTime.now().plusHours(3));
        offre.setStatutOffre(StatutOffre.ACTIVE);
        offre.setOffreur(offreur); // appartient à offreur (id=1)
    }

    // ─────────────────────────────────────────────
    //  TESTS : creerOffre()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("creerOffre() initialise quantiteRestante = quantiteInitiale")
    void creerOffre_doit_initialiser_quantite_restante_egale_initiale() {
        // Arrange — mock de la sauvegarde
        when(offreRepository.save(any(Offre.class)))
                .thenAnswer(inv -> {
                    Offre o = inv.getArgument(0);
                    o.setId(1L);
                    return o;
                });

        // Act — l'offreur crée une offre
        offreService.creerOffre(request, offreur);

        // Assert — la sauvegarde est bien appelée avec les bonnes valeurs
        verify(offreRepository).save(argThat(o ->
                o.getQuantiteInitiale().equals(10) &&
                        o.getQuantiteRestante().equals(10) && // restante = initiale
                        o.getStatutOffre() == StatutOffre.ACTIVE
        ));
    }

    @Test
    @DisplayName("creerOffre() rejette si l'utilisateur n'est pas OFFREUR")
    void creerOffre_doit_rejeter_si_pas_offreur() {
        // Arrange — un étudiant tente de créer une offre
        Utilisateur etudiant = new Utilisateur();
        etudiant.setId(2L);
        etudiant.setRole(Role.ETUDIANT);

        // Act & Assert — AccesRefuseException doit être levée immédiatement
        assertThatThrownBy(() -> offreService.creerOffre(request, etudiant))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("Seul un offreur peut publier");

        // La base ne doit jamais être consultée
        verify(offreRepository, never()).save(any());
    }

    @Test
    @DisplayName("creerOffre() rejette si la fin du créneau est avant le début")
    void creerOffre_doit_rejeter_creneau_incoherent() {
        // Arrange — fin AVANT le début (incohérent)
        request.setDebutRetrait(LocalDateTime.now().plusHours(3));
        request.setFinRetrait(LocalDateTime.now().plusHours(1)); // fin < début

        // Act & Assert — CreneauInvalideException doit être levée
        assertThatThrownBy(() -> offreService.creerOffre(request, offreur))
                .isInstanceOf(CreneauInvalideException.class)
                .hasMessageContaining("postérieure au début");

        verify(offreRepository, never()).save(any());
    }

    @Test
    @DisplayName("creerOffre() rejette si le début du créneau est dans le passé")
    void creerOffre_doit_rejeter_debut_dans_le_passe() {
        // Arrange — début dans le passé
        request.setDebutRetrait(LocalDateTime.now().minusHours(1)); // passé
        request.setFinRetrait(LocalDateTime.now().plusHours(2));

        // Act & Assert — CreneauInvalideException doit être levée
        assertThatThrownBy(() -> offreService.creerOffre(request, offreur))
                .isInstanceOf(CreneauInvalideException.class)
                .hasMessageContaining("passé");

        verify(offreRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    //  TESTS : rechercherOffreParId()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("rechercherOffreParId() retourne l'offre si elle existe")
    void rechercherOffreParId_doit_retourner_offre_existante() {
        // Arrange — l'offre existe en base
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        // Act
        offreService.rechercherOffreParId(10L);

        // Assert — le repository a bien été appelé
        verify(offreRepository).findById(10L);
    }

    @Test
    @DisplayName("rechercherOffreParId() rejette si l'offre est introuvable")
    void rechercherOffreParId_doit_rejeter_si_introuvable() {
        // Arrange — aucune offre avec cet id
        when(offreRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> offreService.rechercherOffreParId(99L))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Offre introuvable");
    }

    @Test
    @DisplayName("modifierOffre() met à jour les champs de l'offre")
    void modifierOffre_doit_mettre_a_jour_les_champs() {
        // Arrange — l'offre existe et appartient à l'offreur
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));
        when(offreRepository.save(any(Offre.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        request.setTitre("Nouveau titre");
        request.setPrix(new BigDecimal("200.00"));

        // Act
        offreService.modifierOffre(10L, request, offreur);

        // Assert — la sauvegarde est bien appelée
        verify(offreRepository).save(argThat(o ->
                o.getTitre().equals("Nouveau titre") &&
                        o.getPrix().equals(new BigDecimal("200.00"))
        ));
    }

    @Test
    @DisplayName("modifierOffre() rejette si l'offreur n'est pas propriétaire")
    void modifierOffre_doit_rejeter_si_pas_proprietaire() {
        // Arrange — l'offre appartient à offreur (id=1) mais autreOffreur (id=99) tente de modifier
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        // Act & Assert
        assertThatThrownBy(() -> offreService.modifierOffre(10L, request, autreOffreur))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("pas autorisé à modifier");

        verify(offreRepository, never()).save(any());
    }

    @Test
    @DisplayName("modifierOffre() rejette si l'offre n'est pas ACTIVE")
    void modifierOffre_doit_rejeter_si_offre_non_active() {
        // Arrange — l'offre est annulée
        offre.setStatutOffre(StatutOffre.ANNULEE);
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        // Act & Assert — on ne peut pas modifier une offre annulée
        assertThatThrownBy(() -> offreService.modifierOffre(10L, request, offreur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("annulee");

        verify(offreRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    //  TESTS : annulerOffre()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("annulerOffre() passe le statut à ANNULEE")
    void annulerOffre_doit_passer_statut_a_annulee() {
        // Arrange — l'offre est ACTIVE et appartient à l'offreur
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));
        when(offreRepository.save(any(Offre.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        offreService.annulerOffre(10L, offreur);

        // Assert — le statut est bien passé à ANNULEE
        assertThat(offre.getStatutOffre()).isEqualTo(StatutOffre.ANNULEE);
        verify(offreRepository).save(offre);
    }

    @Test
    @DisplayName("annulerOffre() rejette si l'offreur n'est pas propriétaire")
    void annulerOffre_doit_rejeter_si_pas_proprietaire() {
        // Arrange — l'offre appartient à offreur (id=1) mais autreOffreur (id=99) tente d'annuler
        when(offreRepository.findById(10L))
                .thenReturn(Optional.of(offre));

        // Act & Assert
        assertThatThrownBy(() -> offreService.annulerOffre(10L, autreOffreur))
                .isInstanceOf(AccesRefuseException.class)
                .hasMessageContaining("pas autorisé à annuler");

        // Le statut ne doit pas avoir changé
        assertThat(offre.getStatutOffre()).isEqualTo(StatutOffre.ACTIVE);
        verify(offreRepository, never()).save(any());
    }


}