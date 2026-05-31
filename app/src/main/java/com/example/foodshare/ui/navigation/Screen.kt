package com.example.foodshare.ui.navigation

// Simple sealed class to represent navigation routes in the app
sealed class Screen(val route: String) {
	object Login : Screen("login")
	object Register : Screen("register")
	object Home : Screen("home")
	object Profile : Screen("profile")
	object ReservationHistory : Screen("reservation_history")
}