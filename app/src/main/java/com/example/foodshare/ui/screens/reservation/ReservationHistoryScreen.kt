package com.example.foodshare.ui.screens.reservation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.composed
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.GrayText
import com.example.foodshare.ui.theme.OrangeAccent
import com.google.gson.Gson

@Composable
fun ReservationHistoryScreen(
	onBackClick: (() -> Unit)? = null
) {
	val context = androidx.compose.ui.platform.LocalContext.current
	val sessionManager = remember(context) { SessionManager(context) }
	var isLoading by remember { mutableStateOf(true) }
	var errorMessage by remember { mutableStateOf<String?>(null) }
	var reservations by remember { mutableStateOf<List<ReservationDto>>(emptyList()) }

	LaunchedEffect(Unit) {
		isLoading = true
		errorMessage = null
		val userJson = sessionManager.getUserJson()
		val user: UserDto? = runCatching { Gson().fromJson(userJson, UserDto::class.java) }.getOrNull()
		val repo = ReservationRepository()
		val result = repo.fetchUserReservations(user?.id)
		if (result.isSuccess) {
			reservations = result.getOrDefault(emptyList())
			isLoading = false
		} else {
			errorMessage = result.exceptionOrNull()?.message ?: "Erreur lors du chargement"
			isLoading = false
		}
	}

	Column(modifier = Modifier.fillMaxWidth()) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Icon(
				imageVector = Icons.Default.ArrowBack,
				contentDescription = "Retour",
				modifier = Modifier
					.padding(4.dp)
					.clickableWithoutRipple { onBackClick?.invoke() },
				tint = OrangeAccent
			)

			Text(text = "Mes réservations", modifier = Modifier.padding(start = 8.dp))

			Spacer(modifier = Modifier.height(4.dp))
		}

		if (isLoading) {
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
		}

		if (!errorMessage.isNullOrBlank()) {
			Card(
				colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFF4A2320)),
				modifier = Modifier
					.fillMaxWidth()
					.padding(12.dp)
			) {
				Text(
					text = errorMessage.orEmpty(),
					color = androidx.compose.ui.graphics.Color(0xFFE6755A),
					modifier = Modifier.padding(12.dp)
				)
			}
		}

		LazyColumn(modifier = Modifier.padding(12.dp)) {
			items(reservations) { r ->
				ReservationItem(r)
			}
		}
	}
}

@Composable
private fun ReservationItem(reservation: ReservationDto) {
	Card(
		colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(text = reservation.offreTitre ?: "Offre inconnue", color = androidx.compose.ui.graphics.Color.Black)
			Text(text = reservation.dateReservation ?: "-", color = GrayText)
			Text(text = reservation.statut ?: "-", color = OrangeAccent)
		}
	}
}

// Small helper to avoid pulling in ripple import; use clickable from foundation with indication = null

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
	val interactionSource = remember { MutableInteractionSource() }
	this.then(clickable(interactionSource = interactionSource, indication = null, onClick = onClick))
}