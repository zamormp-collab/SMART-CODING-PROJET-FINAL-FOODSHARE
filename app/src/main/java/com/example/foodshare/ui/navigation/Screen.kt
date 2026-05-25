package com.example.foodshare.ui.navigation

// Simple sealed class to represent navigation routes in the app
sealed class Screen(val route: String) {
	object Login : Screen("login")
	object Register : Screen("register")
	object Home : Screen("home")
	object Offres : Screen("offres")
	object OffreDetail : Screen("offre_detail/{offreId}") {
		fun createRoute(offreId: String) = "offre_detail/$offreId"
	}
	object Reservation : Screen("reservation")
	object ReservationDetail : Screen("reservation_detail/{reservationId}") {
		fun createRoute(reservationId: String) = "reservation_detail/$reservationId"
	}
	object Profile : Screen("profile")
	object Historique : Screen("historique")
}