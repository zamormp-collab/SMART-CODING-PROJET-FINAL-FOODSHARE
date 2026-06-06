package com.example.foodshare.viewmodel

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.repository.ReservationRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitaires du ReservationViewModel.
 * Pattern AAA (Arrange / Act / Assert)
 *
 * Règles testées :
 *  - État initial est Idle
 *  - Chargement réussi → Success avec liste
 *  - Erreur réseau → Error
 *  - JSON utilisateur corrompu → Error
 *  - Session nulle → Success (null passé au repository)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReservationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ReservationRepository = mockk()
    private val sessionManager: SessionManager = mockk()
    private lateinit var viewModel: ReservationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReservationViewModel(repository, sessionManager)
    }

    @After
    fun tearDown() {
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
        assertTrue(viewModel.uiState is ReservationState.Idle)
    }

    @Test
    fun `loadReservations produit etat Success si chargement reussi`() = runTest {
        // Arrange — session valide + repository retourne une liste
        val userJson = """{"id":"user123","nom":"Jean","email":"jean@uh.ht"}"""
        every { sessionManager.getUserJson() } returns userJson
        coEvery { repository.fetchUserReservations("user123") } returns
                Result.success(emptyList<ReservationDto>())

        // Act
        viewModel.loadReservations()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState is ReservationState.Success)
    }

    @Test
    fun `loadReservations produit etat Error si le repository echoue`() = runTest {
        // Arrange — session valide mais erreur réseau
        val userJson = """{"id":"user123","nom":"Jean","email":"jean@uh.ht"}"""
        every { sessionManager.getUserJson() } returns userJson
        coEvery { repository.fetchUserReservations("user123") } returns
                Result.failure(Exception("Erreur réseau"))

        // Act
        viewModel.loadReservations()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState is ReservationState.Error)
    }

    @Test
    fun `loadReservations produit etat Error si JSON utilisateur est corrompu`() = runTest {
        // Arrange — JSON invalide dans la session
        every { sessionManager.getUserJson() } returns "json_corrompu{{{"

        // Act
        viewModel.loadReservations()
        advanceUntilIdle()

        // Assert — exception de parsing capturée
        assertTrue(viewModel.uiState is ReservationState.Error)
    }

    @Test
    fun `loadReservations fonctionne si la session est nulle`() = runTest {
        // Arrange — pas de session (utilisateur non connecté)
        every { sessionManager.getUserJson() } returns null
        coEvery { repository.fetchUserReservations(null) } returns
                Result.success(emptyList<ReservationDto>())

        // Act
        viewModel.loadReservations()
        advanceUntilIdle()

        // Assert — null transmis sans planter
        assertTrue(viewModel.uiState is ReservationState.Success)
    }
}