package com.example.foodshare.ui.screens.reservation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText
import kotlinx.coroutines.launch

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
	var reservation by remember { mutableStateOf<ReservationDto?>(null) }
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
			.background(Color(0xFFF5F1EC))
			.safeDrawingPadding()
	) {
		HeaderSection(onBackClick = onBackClick)

		Surface(
			modifier = Modifier
				.fillMaxSize(),
				color = BrownPrimary,
				shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(horizontal = 16.dp, vertical = 18.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				AnimatedVisibility(
					visible = loading,
					enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
					exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
				) {
					LoadingSkeleton()
				}

				AnimatedVisibility(
					visible = !errorMessage.isNullOrBlank(),
					enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
					exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
				) {
					ErrorStateCard(message = errorMessage.orEmpty())
				}

				AnimatedVisibility(
					visible = reservation != null && errorMessage.isNullOrBlank() && !loading,
					enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
					exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
				) {
					reservation?.let { res ->
						Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
							MainReservationCard(reservation = res)
							TimelineSection(status = res.statut)

							if (canCancel(res.statut)) {
								Button(
									onClick = {
										coroutineScope.launch {
											loading = true
											val cancelResult = repository.cancelReservation(res.id.orEmpty())
											loading = false
											if (cancelResult.isSuccess) {
												reservation = res.copy(statut = "Annulée")
											} else {
												errorMessage = cancelResult.exceptionOrNull()?.message ?: "Impossible d'annuler"
											}
										}
									},
									colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB84A4A)),
									shape = RoundedCornerShape(18.dp),
									modifier = Modifier.fillMaxWidth()
								) {
									Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = WhiteText)
									Spacer(modifier = Modifier.width(8.dp))
									Text(text = "Annuler la réservation")
								}
							}
						}
					}
				}
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReservationDetailsPreview() {
	FoodShareTheme {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(Color(0xFFF5F1EC))
		) {
			HeaderSection(onBackClick = {})
			Surface(
				modifier = Modifier.fillMaxSize(),
				color = BrownPrimary,
				shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.verticalScroll(rememberScrollState())
						.padding(horizontal = 16.dp, vertical = 18.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					MainReservationCard(
						reservation = ReservationDto(
							id = "r1",
							offreId = "1",
							offreTitre = "Preview Burger",
							dateReservation = "30/05/2026 12:00",
							statut = "Confirmée"
						)
					)
					TimelineSection(status = "Confirmée")
					Button(
						onClick = {},
						colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB84A4A)),
						shape = RoundedCornerShape(18.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = WhiteText)
						Spacer(modifier = Modifier.width(8.dp))
						Text(text = "Annuler la réservation")
					}
				}
			}
		}
	}
}

@Composable
private fun HeaderSection(onBackClick: () -> Unit) {
	Surface(
		modifier = Modifier.fillMaxWidth(),
		color = Color(0xFFF8F4EE),
		shadowElevation = 4.dp
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 16.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(14.dp)
			) {
				IconButton(
					onClick = onBackClick,
					modifier = Modifier
						.size(48.dp)
						.clip(RoundedCornerShape(16.dp))
						.background(Color.White)
				) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color(0xFF222222))
				}

				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = "Détail de la réservation",
						color = Color(0xFF1F1F1F),
						fontSize = 24.sp,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = "Informations complètes de votre réservation",
						color = Color(0xFF7A7A7A),
						fontSize = 13.sp
					)
				}

				Box(
					modifier = Modifier
						.size(54.dp)
						.clip(RoundedCornerShape(18.dp))
						.background(Color.White),
					contentAlignment = Alignment.Center
				) {
																													Icon(Icons.Filled.History, contentDescription = null, tint = BrownPrimary)
				}
			}
		}
	}
}

@Composable
private fun MainReservationCard(reservation: ReservationDto) {
	val (badgeBg, badgeTextColor, badgeIcon) = statusStyle(reservation.statut)

	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		modifier = Modifier
			.fillMaxWidth()
			.animateContentSize()
		) {
		Column(
			modifier = Modifier.padding(18.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Box(
					modifier = Modifier
						.size(52.dp)
						.clip(RoundedCornerShape(18.dp))
						.background(Color(0xFFF8F4EE)),
					contentAlignment = Alignment.Center
				) {
					Icon(Icons.Filled.Restaurant, contentDescription = null, tint = OrangeAccent)
				}
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = reservation.offreTitre ?: "Réservation",
						color = Color(0xFF1F1F1F),
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis
					)
					Text(
						text = reservation.dateReservation ?: "Date non renseignée",
						color = Color(0xFF7A7A7A),
						fontSize = 13.sp
					)
				}
			}

			DetailRow(icon = Icons.Filled.Restaurant, label = "Offre", value = reservation.offreTitre ?: "-")
			DetailRow(icon = Icons.Filled.History, label = "Date", value = reservation.dateReservation ?: "-")
			DetailRow(icon = Icons.Filled.History, label = "Référence", value = reservation.id ?: "-")

			StatusBadge(
				text = reservation.statut ?: "Statut",
				backgroundColor = badgeBg,
				textColor = badgeTextColor,
				icon = badgeIcon
			)
		}
	}
}

@Composable
private fun DetailRow(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	label: String,
	value: String
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Box(
			modifier = Modifier
				.size(40.dp)
				.clip(RoundedCornerShape(14.dp))
				.background(Color(0xFFF8F4EE)),
			contentAlignment = Alignment.Center
		) {
			Icon(icon, contentDescription = null, tint = BrownPrimary)
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(text = label, color = Color(0xFF7A7A7A), fontSize = 12.sp)
			Text(text = value, color = Color(0xFF1F1F1F), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
		}
	}
}

@Composable
private fun StatusBadge(
	text: String,
	backgroundColor: Color,
	textColor: Color,
	icon: androidx.compose.ui.graphics.vector.ImageVector
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier
			.clip(RoundedCornerShape(999.dp))
			.background(backgroundColor)
			.padding(horizontal = 14.dp, vertical = 10.dp)
	) {
		Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
		Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
	}
}

@Composable
private fun TimelineSection(status: String?) {
	val activeStep = when {
		status.isConfirmed() -> 3
		status.isPending() -> 2
		status.isCancelled() -> 1
		status?.contains("refus", ignoreCase = true) == true -> 1
		else -> 1
	}

	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
			Text(text = "Timeline de la réservation", color = Color(0xFF1F1F1F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
			TimelineStep(index = 1, active = true, title = "Réservation créée", subtitle = "Demande enregistrée")
			TimelineStep(index = 2, active = activeStep >= 2, title = "Traitement", subtitle = "En cours de vérification")
			TimelineStep(index = 3, active = activeStep >= 3, title = if (status.isCancelled()) "Annulée" else "Confirmée", subtitle = "Statut final")
		}
	}
}

@Composable
private fun TimelineStep(index: Int, active: Boolean, title: String, subtitle: String) {
	Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
		Box(
			modifier = Modifier
				.size(24.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(if (active) OrangeAccent else Color(0xFFE0E0E0)),
			contentAlignment = Alignment.Center
		) {
			Text(text = index.toString(), color = if (active) WhiteText else Color(0xFF777777), fontSize = 11.sp, fontWeight = FontWeight.Bold)
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(text = title, color = Color(0xFF1F1F1F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
			Text(text = subtitle, color = Color(0xFF7A7A7A), fontSize = 12.sp)
		}
	}
}

@Composable
private fun LoadingSkeleton() {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		SkeletonCard(height = 190.dp)
		SkeletonCard(height = 160.dp)
		SkeletonCard(height = 130.dp)
	}
}

@Composable
private fun SkeletonCard(height: androidx.compose.ui.unit.Dp) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		modifier = Modifier
			.fillMaxWidth()
			.height(height)
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
			Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFEFEFEF)))
			Box(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE8E8E8)))
			Box(modifier = Modifier.fillMaxWidth(0.48f).height(14.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0)))
			Box(modifier = Modifier.fillMaxWidth(0.9f).height(12.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F0F0)))
			Box(modifier = Modifier.fillMaxWidth(0.35f).height(34.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFEAEAEA)))
		}
	}
}

@Composable
private fun ErrorStateCard(message: String) {
	Card(
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF4A2320)),
		modifier = Modifier.fillMaxWidth()
	) {
		Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Color(0x26FFFFFF)), contentAlignment = Alignment.Center) {
				Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = WhiteText)
			}
			Text(text = message, color = WhiteText, modifier = Modifier.weight(1f))
		}
	}
}

private fun canCancel(status: String?): Boolean = status?.contains("annul", ignoreCase = true) != true


@Composable
private fun statusStyle(status: String?): Triple<Color, Color, androidx.compose.ui.graphics.vector.ImageVector> {
	return when {
		status.isConfirmed() -> Triple(Color(0x1A2E7D32), Color(0xFF2E7D32), Icons.Filled.CheckCircle)
		status.isPending() -> Triple(Color(0x1AFFA000), Color(0xFFF57C00), Icons.Filled.HourglassTop)
		status.isCancelled() -> Triple(Color(0x1AD32F2F), Color(0xFFD32F2F), Icons.Filled.ErrorOutline)
		status?.contains("refus", ignoreCase = true) == true -> Triple(Color(0x1A8E1B1B), Color(0xFF8E1B1B), Icons.Filled.WarningAmber)
		else -> Triple(Color(0x1A616161), Color(0xFF616161), Icons.Filled.History)
	}
}

private fun String?.isConfirmed(): Boolean = this?.contains("confirm", ignoreCase = true) == true
private fun String?.isPending(): Boolean = this?.contains("attente", ignoreCase = true) == true || this?.contains("pending", ignoreCase = true) == true
private fun String?.isCancelled(): Boolean = this?.contains("annul", ignoreCase = true) == true
