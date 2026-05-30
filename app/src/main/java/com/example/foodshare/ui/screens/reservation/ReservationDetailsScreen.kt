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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodshare.ui.theme.FoodShareTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp

@Composable
fun ReservationDetailsScreen(
	reservationId: String?,
	onBackClick: () -> Unit = {}
) {
	val context = LocalContext.current
	val sessionManager = remember(context) { SessionManager(context) }
	val repository = remember(sessionManager) { ReservationRepository(sessionManager) }

	val coroutineScope = rememberCoroutineScope()
	var loading by remember { mutableStateOf(true) }
	var reservation by remember { mutableStateOf<com.example.foodshare.data.remote.dto.ReservationDto?>(null) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(reservationId) {
		loading = true
		errorMessage = null
		if (reservationId.isNullOrBlank()) {
			errorMessage = "Identifiant de réservation invalide"
		} else {
			val result = repository.fetchReservationById(reservationId)
			if (result.isSuccess) {
				reservation = result.getOrNull()!!
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
			reservation != null -> {
				val res = reservation!!
				Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
					Column(modifier = Modifier.padding(18.dp)) {
						Text(text = "Offre : ${res.offreTitre ?: "-"}", color = WhiteText)
						Text(text = "Date : ${res.dateReservation ?: "-"}", color = WhiteText)
						Text(text = "Statut : ${res.statut ?: "-"}", color = WhiteText)
						Text(text = "ID réservation : ${res.id ?: "-"}", color = WhiteText)
					}
				}

				// Bouton d'annulation si possible
				if (!res.statut.isNullOrBlank() && !res.statut.contains("annul", ignoreCase = true)) {
					Button(onClick = {
						coroutineScope.launch {
							loading = true
							val cancelResult = repository.cancelReservation(res.id.orEmpty())
							loading = false
							if (cancelResult.isSuccess) {
								// Met à jour l'état local pour refléter l'annulation
								reservation = res.copy(statut = "Annulée")
							} else {
								errorMessage = cancelResult.exceptionOrNull()?.message ?: "Impossible d'annuler"
							}
						}
					}, colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)) {
						Text(text = "Annuler la réservation")
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ReservationDetailsPreview() {
	FoodShareTheme {
		Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
			val res = com.example.foodshare.data.remote.dto.ReservationDto(id = "r1", offreId = "1", offreTitre = "Preview Burger", dateReservation = "30/05/2026 12:00", statut = "Confirmée")
			Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
				Column(modifier = Modifier.padding(18.dp)) {
					Text(text = "Offre : ${res.offreTitre}", color = WhiteText)
					Text(text = "Date : ${res.dateReservation}", color = WhiteText)
					Text(text = "Statut : ${res.statut}", color = WhiteText)
					Text(text = "ID réservation : ${res.id}", color = WhiteText)
				}
			}
			Button(onClick = {}, modifier = Modifier.padding(top = 12.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)) {
				Text(text = "Annuler la réservation")
			}
		}
	}
}
