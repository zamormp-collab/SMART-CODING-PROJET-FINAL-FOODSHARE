package com.example.foodshare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foodshare.ui.screens.auth.LoginScreen
import com.example.foodshare.ui.screens.auth.RegisterScreen
import com.example.foodshare.ui.screens.home.HomeScreen
// Note: For now we only register the auth and home destinations.
// Other screens (Offres, Reservation, Profile, Historique) will be added branch-by-branch later.
// Note: For now we only register the auth and home destinations.
// Other screens (Offres, Reservation, Profile, Historique) will be added branch-by-branch later.
import com.example.foodshare.viewmodel.AuthViewModel
import com.example.foodshare.viewmodel.RegisterViewModel
import com.example.foodshare.viewmodel.LoginState

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    registerViewModel: RegisterViewModel,
    sessionManager: SessionManager
) {
    val startDestination = if (authViewModel.uiState is LoginState.Success) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Ensure Home destination is registered before any navigate calls
        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // Note: keeping NavGraph intentionally minimal for now
    }
}