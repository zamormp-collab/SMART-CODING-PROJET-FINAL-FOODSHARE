package com.example.foodshare.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodshare.ui.navigation.BottomNavBar
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() },
        containerColor = DarkBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Welcome to FoodShare",
                color = WhiteText,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn {
                items(5) {
                    OffreCard()
                }
            }
        }
    }
}