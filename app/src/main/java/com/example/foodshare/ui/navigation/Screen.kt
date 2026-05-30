package com.example.foodshare.ui.navigation

sealed class Screen(val route: String) {
	object Login : Screen("login")
	object Register : Screen("register")
	object Home : Screen("home")
	object Reservation : Screen("reservation")
	object Profile : Screen("profile")
	object OffreDetail : Screen("offre_detail/{offreId}") {
		fun createRoute(offreId: String) = "offre_detail/$offreId"
	}
}