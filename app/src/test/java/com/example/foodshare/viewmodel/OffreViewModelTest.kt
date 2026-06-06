package com.example.foodshare.viewmodel

import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.repository.OffreRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitaires du OffreViewModel.
 * Pattern AAA (Arrange / Act / Assert)
 *
 * Règles testées :
 *  - État initial est Idle
 *  - Id null ou vide → Error immédiat
 *  - Chargement réussi → Success avec l'offre
 *  - Erreur réseau → Error avec le message
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OffreViewModelTest {

    // Dispatcher de test — contrôle l'exécution des coroutines
    private val testDispatcher = StandardTestDispatcher()

    // Mock du repository — simule les appels réseau
    private val repository: OffreRepository = mockk()

    // Instance du ViewModel à tester
    private lateinit var viewModel: OffreViewModel

    // Offre exemple réutilisée dans les tests
    private val offreExemple = OffreDto(
        id = "42",
        title = "Sandwich poulet",
        description = "Pain complet",
        quantity = 5,
        expirationDate = "2026-06-12",
        location = "Cafétéria",
        imageUrl = null,
        userId = "user-1"
    )

    @Before
    fun setUp() {
        // Remplacer le dispatcher Main par le dispatcher de test
        Dispatchers.setMain(testDispatcher)
        viewModel = OffreViewModel(repository)
    }

    @After
    fun tearDown() {
        // Restaurer le dispatcher original
        Dispatchers.resetMain()
    }

    // ─────────────────────────────────────────────
    //  TESTS
    // ─────────────────────────────────────────────

    @Test
    fun `etat initial est Idle`() {
        // Arrange — rien à préparer

        // Act — aucune action

        // Assert
        assertTrue(viewModel.uiState is OffreDetailState.Idle)
    }

    @Test
    fun `loadOffer avec id null produit etat Error`() = runTest {
        // Arrange — id null (invalide)

        // Act
        viewModel.loadOffer(null)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState
        assertTrue(state is OffreDetailState.Error)
        assertEquals(
            "Identifiant d'offre invalide",
            (state as OffreDetailState.Error).message
        )
    }

    @Test
    fun `loadOffer avec id vide produit etat Error`() = runTest {
        // Arrange — id vide (invalide)

        // Act
        viewModel.loadOffer("")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState
        assertTrue(state is OffreDetailState.Error)
        assertEquals(
            "Identifiant d'offre invalide",
            (state as OffreDetailState.Error).message
        )
    }

    @Test
    fun `loadOffer avec id valide produit etat Success`() = runTest {
        // Arrange — le repository retourne une offre valide
        coEvery { repository.fetchOffreById("42") } returns Result.success(offreExemple)

        // Act
        viewModel.loadOffer("42")
        advanceUntilIdle()

        // Assert — état Success avec l'offre attendue
        val state = viewModel.uiState
        assertTrue(state is OffreDetailState.Success)
        assertEquals("Sandwich poulet", (state as OffreDetailState.Success).offer.title)
    }

    @Test
    fun `loadOffer produit etat Error si le repository echoue`() = runTest {
        // Arrange — le repository retourne une erreur
        coEvery { repository.fetchOffreById("99") } returns
                Result.failure(Exception("Serveur indisponible"))

        // Act
        viewModel.loadOffer("99")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState
        assertTrue(state is OffreDetailState.Error)
        assertEquals(
            "Serveur indisponible",
            (state as OffreDetailState.Error).message
        )
    }
}