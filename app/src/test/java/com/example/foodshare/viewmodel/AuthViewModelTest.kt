package com.example.foodshare.viewmodel

import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.api.AuthApiService
import com.example.foodshare.data.remote.dto.AuthResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Tests unitaires du AuthViewModel.
 * Pattern AAA (Arrange / Act / Assert)
 *
 * Note : android.util.Log est mocké via mockkStatic
 * car indisponible dans les tests JVM.
 *
 * Règles testées :
 *  - Login réussi → Success + token sauvegardé
 *  - Login 401 → Error "Invalid email or password"
 *  - Login timeout → Error avec message réseau
 *  - Login ConnectException → Error avec message connexion
 *  - Logout → Idle + session effacée
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authApiService: AuthApiService = mockk()

    // relaxed = true → appels void (saveToken, clearSession) ignorés sans erreur
    private val sessionManager: SessionManager = mockk(relaxed = true)

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mocker android.util.Log — non disponible en tests JVM
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

        // Pas de token existant → init ne déclenche pas d'appel réseau
        every { sessionManager.getToken() } returns null

        viewModel = AuthViewModel(authApiService, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─────────────────────────────────────────────
    //  TESTS : login()
    // ─────────────────────────────────────────────

    @Test
    fun `login reussi produit etat Success`() = runTest {
        // Arrange — API retourne 200 avec un token
        val authResponse = AuthResponse(
            token = "jwt-token-valide",
            userId = "user-123",
            role = "ETUDIANT",
            message = null
        )
        coEvery { authApiService.login(any()) } returns Response.success(authResponse)

        // Act
        viewModel.login("jean@uh.ht", "password123")
        advanceUntilIdle()

        // Assert — état Success
        assertTrue(viewModel.uiState is LoginState.Success)

        // Et le token a été sauvegardé en session
        verify { sessionManager.saveToken("jwt-token-valide") }
    }

    @Test
    fun `login avec code 401 produit etat Error`() = runTest {
        // Arrange — API retourne 401 (mauvais identifiants)
        coEvery { authApiService.login(any()) } returns
                Response.error(401, "Unauthorized".toResponseBody())

        // Act
        viewModel.login("jean@uh.ht", "mauvais_mdp")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState
        assertTrue(state is LoginState.Error)
        assertEquals(
            "Invalid email or password",
            (state as LoginState.Error).message
        )
    }

    @Test
    fun `login avec SocketTimeoutException produit etat Error`() = runTest {
        // Arrange — serveur ne répond pas
        coEvery { authApiService.login(any()) } throws SocketTimeoutException()

        // Act
        viewModel.login("jean@uh.ht", "password123")
        advanceUntilIdle()

        // Assert — message contient "timeout"
        val state = viewModel.uiState
        assertTrue(state is LoginState.Error)
        assertTrue((state as LoginState.Error).message.contains("timeout"))
    }

    @Test
    fun `login avec ConnectException produit etat Error`() = runTest {
        // Arrange — serveur inaccessible
        coEvery { authApiService.login(any()) } throws ConnectException()

        // Act
        viewModel.login("jean@uh.ht", "password123")
        advanceUntilIdle()

        // Assert — message contient "Connection error"
        val state = viewModel.uiState
        assertTrue(state is LoginState.Error)
        assertTrue((state as LoginState.Error).message.contains("Connection error"))
    }

    // ─────────────────────────────────────────────
    //  TESTS : logout()
    // ─────────────────────────────────────────────

    @Test
    fun `logout produit etat Idle et efface la session`() {
        // Arrange — rien à préparer

        // Act
        viewModel.logout()

        // Assert — retour à l'état initial
        assertTrue(viewModel.uiState is LoginState.Idle)

        // Et la session est effacée
        verify { sessionManager.clearSession() }
    }
}