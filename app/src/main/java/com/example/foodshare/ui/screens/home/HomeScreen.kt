package com.example.foodshare.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.foodshare.viewmodel.UserViewModel
import com.example.foodshare.viewmodel.UserState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshare.viewmodel.UserViewModelFactory
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val sessionManager = com.example.foodshare.data.local.SessionManager(context)
    val factory = UserViewModelFactory(sessionManager)
    val userViewModel: UserViewModel = viewModel(factory = factory)

    // Local UI state derived from userViewModel
    var displayName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Try cached user first
        when (val cached = userViewModel.getCachedUser()) {
            is UserState.Success -> displayName = cached.user.nom
            else -> {
                // load from API
                userViewModel.loadCurrentUser()
                // small delay to let state update
                kotlinx.coroutines.delay(200)
                when (val s = userViewModel.uiState) {
                    is UserState.Success -> displayName = s.user.nom
                    else -> {}
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B5E3C),  // BrownPrimary
                        Color(0xFF121212)   // DarkBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = displayName?.let { "Bienvenue $it" } ?: "Bienvenue à FoodShare",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                text = "Découvrez les offres disponibles près de vous",
                fontSize = 14.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Featured Offres
            Text(
                text = "Offres à la une",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(3) {
                    FeaturedOffreCard()
                }
            }
        }
    }
}

@Composable
private fun FeaturedOffreCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = "Offre du jour",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gratuit",
                    fontSize = 14.sp,
                    color = Color(0xFFE6A15A),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { /* TODO: Navigate to Offres */ },
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A15A))
            ) {
                Text("Voir", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}