package com.example.foodshare.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.foodshare.viewmodel.UserViewModel
import com.example.foodshare.viewmodel.UserState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(userViewModel: UserViewModel) {
    var name by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf<String?>(null) }
    var role by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val cached = userViewModel.getCachedUser()) {
            is UserState.Success -> {
                name = cached.user.nom
                email = cached.user.email
                role = cached.user.role
            }
            else -> {
                userViewModel.loadCurrentUser()
                when (val s = userViewModel.uiState) {
                    is UserState.Success -> {
                        name = (s as UserState.Success).user.nom
                        email = s.user.email
                        role = s.user.role
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Z",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE6A15A)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Name
            Text(
                text = name ?: "Utilisateur",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User Role
            Text(
                text = role ?: "-",
                fontSize = 14.sp,
                color = Color(0xFFE6A15A),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Email
                    ProfileInfoRow(label = "Email", value = email ?: "-")
                    HorizontalDivider(color = Color(0xFF444444), modifier = Modifier.padding(vertical = 12.dp))

                    // Téléphone
                    // Téléphone & Adress may not be present — keep placeholders if absent
                    ProfileInfoRow(label = "Téléphone", value = "-")
                    HorizontalDivider(color = Color(0xFF444444), modifier = Modifier.padding(vertical = 12.dp))

                    // Adresse
                    ProfileInfoRow(label = "Adresse", value = "-")
                    HorizontalDivider(color = Color(0xFF444444), modifier = Modifier.padding(vertical = 12.dp))

                    // Rôle
                    ProfileInfoRow(label = "Rôle", value = role ?: "-")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Button
            Button(
                onClick = { /* TODO: Navigate to Edit Profile */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A15A))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier profil",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    tint = Color.Black
                )
                Text(
                    "Modifier mon profil",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = { /* TODO: Logout */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
            ) {
                Text(
                    "Se déconnecter",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}