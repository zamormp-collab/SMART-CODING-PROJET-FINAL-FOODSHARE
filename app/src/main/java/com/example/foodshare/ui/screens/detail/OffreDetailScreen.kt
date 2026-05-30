package com.example.foodshare.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun OffreDetailScreen(
    offreId: String?,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val repository = remember(sessionManager) { OffreRepository(sessionManager) }

    var loading by remember { mutableStateOf(true) }
    var offer by remember { mutableStateOf<OffreDto?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(offreId) {
        loading = true
        errorMessage = null
        offer = null

        if (offreId.isNullOrBlank()) {
            errorMessage = "Identifiant d'offre invalide"
            loading = false
            return@LaunchedEffect
        }

        val result = repository.fetchOfferById(offreId)
        if (result.isSuccess) {
            offer = result.getOrNull()
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Impossible de charger l'offre"
        }
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(
                Brush.verticalGradient(
                    listOf(BrownPrimary, DarkBackground)
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = WhiteText
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Détail de l'offre",
                color = WhiteText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            loading -> {
                CircularProgressIndicator(color = OrangeAccent)
            }
            errorMessage != null -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = WhiteText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            offer != null -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = offer?.title ?: "Offre",
                            color = WhiteText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = offer?.description ?: "", color = WhiteText)
                        Text(text = "Quantité : ${offer?.quantity ?: 0}", color = WhiteText)
                        Text(text = "Lieu : ${offer?.location ?: "-"}", color = WhiteText)
                        Text(text = "Expiration : ${offer?.expirationDate ?: "-"}", color = WhiteText)
                        Button(
                            onClick = { /* reserve later */ },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                        ) {
                            Text(text = "RÉSERVER")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OffreDetailPreview() {
    FoodShareTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Double Beef",
                        color = WhiteText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Burger maison, frites et sauce spéciale", color = WhiteText)
                    Text(text = "Quantité : 1", color = WhiteText)
                    Text(text = "Lieu : Paris", color = WhiteText)
                    Text(text = "Expiration : 01/06/2026", color = WhiteText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                    ) {
                        Text(text = "RÉSERVER")
                    }
                }
            }
        }
    }
}
