package com.example.foodshare.ui.screens.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodshare.ui.theme.FoodShareTheme

@Composable
fun ReservationScreen(
	onBackClick: () -> Unit = {},
	onReservationClick: (String) -> Unit = {}
) {
	val context = androidx.compose.ui.platform.LocalContext.current
	val sessionManager = remember(context) { SessionManager(context) }
	val reservationRepository = remember(sessionManager) { ReservationRepository(sessionManager) }
	val offerRepository = remember(sessionManager) { OffreRepository(sessionManager) }
	val coroutineScope = rememberCoroutineScope()

	var loading by remember { mutableStateOf(true) }
	var offers by remember { mutableStateOf<List<OffreDto>>(emptyList()) }
	var reservations by remember { mutableStateOf<List<ReservationDto>>(emptyList()) }
	var errorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		loading = true
		val offerResult = offerRepository.fetchOffers()
		val reservationResult = reservationRepository.fetchUserReservations(null)
		offers = offerResult.getOrDefault(emptyList())
		reservations = reservationResult.getOrDefault(emptyList())
		errorMessage = when {
			offerResult.isFailure -> offerResult.exceptionOrNull()?.message
			reservationResult.isFailure -> reservationResult.exceptionOrNull()?.message
			else -> null
		}
		loading = false
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.safeDrawingPadding()
			.background(Brush.verticalGradient(listOf(BrownPrimary, DarkBackground)))
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
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
					modifier = Modifier.size(52.dp).background(OrangeAccent, RoundedCornerShape(18.dp)),
					contentAlignment = Alignment.Center
				) {
					Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = WhiteText)
				}
				Column {
					Text(text = "Mes réservations", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
					Text(text = "Choisissez une offre disponible pour réserver", color = WhiteText)
				}
			}
		}

		Text(text = "Offres disponibles", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
		offers.forEach { offer ->
			OfferReservationCard(
				offer = offer,
				onReserve = {
					coroutineScope.launch {
						val created = reservationRepository.createReservation(offer).getOrNull()
						if (created != null) {
							reservations = listOf(created) + reservations.filterNot { it.id == created.id }
							onReservationClick(created.id.orEmpty())
						}
					}
				}
			)
		}

		if (loading) {
			CircularProgressIndicator(color = OrangeAccent)
		} else if (!errorMessage.isNullOrBlank()) {
			Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
				Text(text = errorMessage!!, color = WhiteText, modifier = Modifier.padding(16.dp))
			}
		}

		Text(text = "Réservations effectuées", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
		if (reservations.isEmpty()) {
			Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
				Text(text = "Aucune réservation pour le moment", color = WhiteText, modifier = Modifier.padding(16.dp))
			}
		} else {
			reservations.forEach { reservation ->
				ReservationItemCard(
					reservation = reservation,
					onClick = { reservation.id?.let(onReservationClick) },
					onCancel = {
						reservation.id?.let { id ->
							coroutineScope.launch {
								val res = reservationRepository.cancelReservation(id)
								if (res.isSuccess) {
									reservations = reservations.filterNot { it.id == id }
								}
							}
						}
					}
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ReservationScreenPreview() {
	FoodShareTheme {
		Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			// Header
			Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
				Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
					Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = WhiteText)
					Spacer(modifier = Modifier.width(12.dp))
					Column {
						Text(text = "Mes réservations", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
						Text(text = "Choisissez une offre disponible pour réserver", color = WhiteText)
					}
				}
			}

			// Sample offer
			OfferReservationCard(
				offer = OffreDto(
					id = "1",
					title = "Preview Burger",
					description = "Un burger délicieux",
					quantity = 1,
					location = "Paris",
					expirationDate = "01/06/2026",
					imageUrl = null,
					userId = null
				),
				onReserve = {}
			)

			// Sample reservation
			ReservationItemCard(reservation = ReservationDto(id = "r1", offreId = "1", offreTitre = "Preview Burger", dateReservation = "30/05/2026 12:00", statut = "Confirmée"), onClick = {}, onCancel = {})
		}
	}
}

@Composable
private fun OfferReservationCard(offer: OffreDto, onReserve: () -> Unit) {
	Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Icon(Icons.Default.Restaurant, contentDescription = null, tint = OrangeAccent)
				Text(text = offer.title ?: "Offre", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
			}
			Text(text = offer.description ?: "Aucune description", color = WhiteText)
			Text(text = "Lieu : ${offer.location ?: "-"}", color = WhiteText)
			Button(onClick = onReserve, colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)) {
				Text(text = "Réserver")
			}
		}
	}
}

@Composable
private fun ReservationItemCard(reservation: ReservationDto, onClick: () -> Unit, onCancel: () -> Unit) {
	Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Icon(Icons.Default.Restaurant, contentDescription = null, tint = OrangeAccent)
				Text(text = reservation.offreTitre ?: "Réservation", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
			}
			Text(text = reservation.dateReservation ?: "Date non renseignée", color = WhiteText)
			Text(text = reservation.statut ?: "En attente", color = WhiteText)
			// Bouton annuler si la réservation n'est pas déjà annulée
			if (reservation.statut.isNullOrBlank() || !reservation.statut.contains("annul", ignoreCase = true)) {
				Spacer(modifier = Modifier.width(8.dp))
				Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)) {
					Text(text = "Annuler")
				}
			}
		}
	}
}