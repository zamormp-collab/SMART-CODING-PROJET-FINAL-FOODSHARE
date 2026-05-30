package com.example.foodshare.ui.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun ReservationDetailsScreen(
	reservationId: String?,
	onBackClick: () -> Unit = {}
) {
	val context = LocalContext.current
	val sessionManager = remember(context) { SessionManager(context) }
	val repository = remember(sessionManager) { ReservationRepository(sessionManager) }

	var loading by remember { mutableStateOf(true) }
	var reservationText by remember { mutableStateOf<String?>(null) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(reservationId) {
		loading = true
		errorMessage = null
		reservationText = null
		if (reservationId.isNullOrBlank()) {
			errorMessage = "Identifiant de réservation invalide"
		} else {
			val result = repository.fetchReservationById(reservationId)
			if (result.isSuccess) {
				val res = result.getOrNull()!!
				reservationText = buildString {
					appendLine("Offre : ${res.offreTitre ?: "-"}")
					appendLine("Date : ${res.dateReservation ?: "-"}")
					appendLine("Statut : ${res.statut ?: "-"}")
					appendLine("ID réservation : ${res.id ?: "-"}")
				}
			} else {
				errorMessage = result.exceptionOrNull()?.message ?: "Impossible de charger le détail"
			}
		}
		loading = false
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.safeDrawingPadding()
			.background(Brush.verticalGradient(listOf(BrownPrimary, DarkBackground)))
			.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)) {
				Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = WhiteText)
			}
			Spacer(modifier = Modifier.width(8.dp))
			Text(text = "Détail réservation", color = WhiteText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
		}

		when {
			loading -> CircularProgressIndicator(color = OrangeAccent)
			errorMessage != null -> Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
				Text(text = errorMessage!!, color = WhiteText, modifier = Modifier.padding(16.dp))
			}
			reservationText != null -> Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
				Text(text = reservationText!!, color = WhiteText, modifier = Modifier.padding(18.dp))
			}
		}
	}
}