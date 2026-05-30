package com.example.foodshare.ui.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun ReservationScreen(
	onBackClick: () -> Unit = {}
) {
	val repository = remember { ReservationRepository() }
	var loading by remember { mutableStateOf(true) }
	var reservations by remember { mutableStateOf<List<ReservationDto>>(emptyList()) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		loading = true
		val result = repository.fetchUserReservations(null)
		if (result.isSuccess) {
			reservations = result.getOrDefault(emptyList())
			errorMessage = null
		} else {
			errorMessage = result.exceptionOrNull()?.message ?: "Impossible de charger les réservations"
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
			Text(text = "Réservations", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
		}

		Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
			Row(
				modifier = Modifier.padding(18.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				Box(
					modifier = Modifier
						.size(52.dp)
						.background(OrangeAccent, RoundedCornerShape(18.dp)),
					contentAlignment = Alignment.Center
				) {
					Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = WhiteText)
				}
				Column {
					Text(text = "Mes réservations", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
					Text(text = "Vos demandes et retraits apparaîtront ici", color = WhiteText)
				}
			}
		}

		if (loading) {
			CircularProgressIndicator(color = OrangeAccent)
		} else if (!errorMessage.isNullOrBlank()) {
			Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
				Text(text = errorMessage!!, color = WhiteText, modifier = Modifier.padding(16.dp))
			}
		} else {
			reservations.forEach { reservation ->
				ReservationItemCard(reservation = reservation)
				Spacer(modifier = Modifier.padding(6.dp))
			}
		}
	}
}

@Composable
private fun ReservationItemCard(reservation: ReservationDto) {
	Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Icon(Icons.Default.Restaurant, contentDescription = null, tint = OrangeAccent)
				Text(text = reservation.offreTitre ?: "Réservation", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
			}
			Text(text = reservation.dateReservation ?: "Date non renseignée", color = WhiteText)
			Text(text = reservation.statut ?: "En attente", color = WhiteText)
		}
	}
}