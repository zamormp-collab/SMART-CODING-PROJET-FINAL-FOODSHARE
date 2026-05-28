package com.example.foodshare.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) },
            icon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
            label = { Text("Offres") }
        )

        NavigationBarItem(
            selected = currentRoute == Screen.Reservation.route,
            onClick = { onNavigate(Screen.Reservation.route) },
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
            label = { Text("Réservations") }
        )
    }
}