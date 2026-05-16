package com.example.foodshare.ui.navigation

// Simple sealed class to represent navigation routes in the app
sealed class Screen(val route: String) {
	object Login : Screen("login")
	object Register : Screen("register")
	object Home : Screen("home")
	// add other screens here as needed, for example:
	// object Profile : Screen("profile")
}