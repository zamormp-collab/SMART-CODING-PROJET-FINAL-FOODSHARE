package com.example.foodshare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foodshare.ui.screens.auth.LoginScreen
import com.example.foodshare.ui.screens.auth.RegisterScreen
import com.example.foodshare.ui.screens.home.HomeScreen
import com.example.foodshare.ui.screens.profile.ProfileScreen
import com.example.foodshare.ui.screens.reservation.ReservationHistoryScreen
import com.example.foodshare.viewmodel.AuthViewModel
import com.example.foodshare.viewmodel.RegisterViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    registerViewModel: RegisterViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Ensure Home destination is registered before any navigate calls
        composable(Screen.Home.route) {
            HomeScreen(onProfileClick = {
                navController.navigate(Screen.Profile.route)
            })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onReservationsClick = { navController.navigate(Screen.ReservationHistory.route) }
            )
        }

        composable(Screen.ReservationHistory.route) {
            ReservationHistoryScreen(onBackClick = { navController.popBackStack() })
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
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // Home is registered above
    }
}