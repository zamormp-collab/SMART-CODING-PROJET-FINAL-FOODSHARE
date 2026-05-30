package com.example.foodshare.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.ui.theme.GrayText
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText
import com.example.foodshare.viewmodel.UserState
import com.example.foodshare.viewmodel.UserViewModel
import com.example.foodshare.viewmodel.UserViewModelFactory

@Composable
fun ProfileScreen(
	onBackClick: () -> Unit = {}
) {
	val context = LocalContext.current
	val sessionManager = remember(context) { SessionManager(context) }
	val userViewModel: UserViewModel = viewModel(factory = remember(sessionManager) { UserViewModelFactory(sessionManager) })
	val cachedUserState = remember { userViewModel.getCachedUser() }
	val userUiState = userViewModel.uiState

	LaunchedEffect(Unit) {
		if (cachedUserState !is UserState.Success && userUiState is UserState.Idle) {
			userViewModel.loadCurrentUser()
		}
	}

	val user: UserDto? = when {
		userUiState is UserState.Success -> userUiState.user
		cachedUserState is UserState.Success -> cachedUserState.user
		else -> null
	}
	val loadingUser = userUiState is UserState.Loading
	val userErrorMessage = (userUiState as? UserState.Error)?.message

	val reservationRepository = remember(sessionManager) { ReservationRepository(sessionManager) }
	var reservations by remember { mutableStateOf<List<ReservationDto>>(emptyList()) }
	var reservationsLoading by remember { mutableStateOf(true) }
	var reservationsErrorMessage by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		reservationsLoading = true
		val result = reservationRepository.fetchUserReservations(user?.id)
		reservations = result.getOrDefault(emptyList())
		reservationsErrorMessage = result.exceptionOrNull()?.message
		reservationsLoading = false
	}

	ProfileScreenContent(
		onBackClick = onBackClick,
		user = user,
		loadingUser = loadingUser,
		userErrorMessage = userErrorMessage,
		reservations = reservations,
		reservationsLoading = reservationsLoading,
		reservationsErrorMessage = reservationsErrorMessage
	)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
	FoodShareTheme {
		ProfileScreenContent(
			onBackClick = {},
			user = UserDto(
				id = "1",
				nom = "Delva",
				prenom = "Patrick",
				email = "patrick@example.com",
				role = "Utilisateur",
				avatarUrl = null
			),
			loadingUser = false,
			userErrorMessage = null,
			reservations = listOf(
				ReservationDto("r1", "o1", "Double Beef", "30/05/2026 12:30", "Confirmée"),
				ReservationDto("r2", "o2", "Chicken Crisp", "29/05/2026 18:00", "En attente"),
				ReservationDto("r3", "o3", "Fresh Salad", "27/05/2026 13:15", "Annulée")
			),
			reservationsLoading = false,
			reservationsErrorMessage = null
		)
	}
}

@Composable
private fun ProfileScreenContent(
	onBackClick: () -> Unit,
	user: UserDto?,
	loadingUser: Boolean,
	userErrorMessage: String?,
	reservations: List<ReservationDto>,
	reservationsLoading: Boolean,
	reservationsErrorMessage: String?
) {
	val scrollState = rememberScrollState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(Color(0xFFF8F4EE))
	) {
		ProfileHeader(
			onBackClick = onBackClick,
			user = user,
			loadingUser = loadingUser
		)

		Surface(
			modifier = Modifier.fillMaxSize(),
			color = BrownPrimary,
			shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(scrollState)
					.padding(horizontal = 16.dp, vertical = 18.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				if (!userErrorMessage.isNullOrBlank()) {
					ErrorStateCard(message = userErrorMessage)
				}

				if (!reservationsErrorMessage.isNullOrBlank()) {
					ErrorStateCard(message = reservationsErrorMessage)
				}

				SectionTitle(title = "Informations personnelles")
				if (loadingUser && user == null) {
					SkeletonPersonalInfo()
				} else if (user != null) {
					CardList(
						items = listOf(
							InfoItem(Icons.Filled.Person, "Nom", user.nom.orDash()),
							InfoItem(Icons.Filled.Person, "Prénom", user.prenom.orDash()),
							InfoItem(Icons.Filled.Email, "Email", user.email.orDash()),
							InfoItem(Icons.Filled.Badge, "Rôle", user.role.orDash())
						)
					)
				} else {
					EmptyStateCard(
						icon = Icons.Filled.Person,
						title = "Aucune information disponible",
						description = "Le profil n’a pas pu être chargé pour le moment."
					)
				}

				SectionTitle(title = "Compte")
				AccountSection()

				SectionTitle(title = "Historique des réservations")
				HistorySummaryCard(reservations = reservations)

				when {
					reservationsLoading -> ReservationSkeletonList()
					reservations.isEmpty() -> EmptyStateCard(
						icon = Icons.Filled.History,
						title = "Aucune réservation effectuée",
						description = "Les réservations validées apparaîtront ici."
					)
					else -> reservations.forEach { reservation ->
						ReservationHistoryCard(reservation = reservation)
					}
				}

				Spacer(modifier = Modifier.height(8.dp))
			}
		}
	}
}

@Composable
private fun ProfileHeader(
	onBackClick: () -> Unit,
	user: UserDto?,
	loadingUser: Boolean
) {
	val displayName = user.displayFullName()
	val displayEmail = user?.email.orDash("Aucune information disponible")
	val displayRole = user?.role.orDash("Rôle non défini")
	val initial = user?.prenom?.firstOrNull()?.uppercaseChar()
		?: user?.nom?.firstOrNull()?.uppercaseChar()
		?: 'U'

	Surface(
		modifier = Modifier.fillMaxWidth(),
		color = Color(0xFFF8F4EE),
		shadowElevation = 4.dp
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.safeDrawingPadding()
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
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "Retour",
						tint = Color(0xFF222222)
					)
				}

				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(
						text = "Mon profil",
						color = Color(0xFF1F1F1F),
						fontSize = 24.sp,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = "Informations du compte et réservations",
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
					Icon(
						imageVector = Icons.Filled.History,
						contentDescription = null,
						tint = BrownPrimary
					)
				}
			}

			Card(
				shape = RoundedCornerShape(28.dp),
				colors = CardDefaults.cardColors(containerColor = Color.White),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
				modifier = Modifier.fillMaxWidth()
			) {
				Column(
					modifier = Modifier.padding(18.dp),
					verticalArrangement = Arrangement.spacedBy(18.dp)
				) {
					Box(
						modifier = Modifier.fillMaxWidth(),
						contentAlignment = Alignment.Center
					) {
						Box(
							modifier = Modifier
								.size(130.dp)
								.clip(CircleShape)
								.background(BrownPrimary),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = initial.toString(),
								color = WhiteText,
								fontSize = 42.sp,
								fontWeight = FontWeight.Bold
							)
						}

						Box(
							modifier = Modifier
								.align(Alignment.BottomEnd)
								.size(34.dp)
								.clip(CircleShape)
								.background(OrangeAccent),
							contentAlignment = Alignment.Center
						) {
							Icon(
								imageVector = Icons.Filled.PhotoCamera,
								contentDescription = null,
								tint = WhiteText,
								modifier = Modifier.size(16.dp)
							)
						}
					}

					Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
						Text(
							text = if (loadingUser && user == null) "Chargement..." else displayName,
							color = Color(0xFF1F1F1F),
							fontSize = 24.sp,
							fontWeight = FontWeight.Bold,
							textAlign = TextAlign.Center
						)
						Spacer(modifier = Modifier.height(4.dp))
						Text(
							text = displayEmail,
							color = Color(0xFF7A7A7A),
							fontSize = 13.sp,
							textAlign = TextAlign.Center,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
						Spacer(modifier = Modifier.height(6.dp))
						Text(
							text = displayRole,
							color = BrownPrimary,
							fontSize = 13.sp,
							fontWeight = FontWeight.SemiBold
						)
					}

					Button(
						onClick = {},
						modifier = Modifier
							.fillMaxWidth()
							.height(56.dp),
						shape = RoundedCornerShape(18.dp),
						colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
					) {
						Icon(
							imageVector = Icons.Filled.PhotoCamera,
							contentDescription = null,
							tint = WhiteText,
							modifier = Modifier.size(18.dp)
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text(text = "Changer la photo", color = WhiteText)
					}
				}
			}
		}
	}
}

@Composable
private fun SectionTitle(title: String) {
	Text(
		text = title,
		color = WhiteText,
		fontSize = 20.sp,
		fontWeight = FontWeight.Bold
	)
}

@Composable
private fun CardList(items: List<InfoItem>) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		items.forEach { item ->
			InfoCard(item = item)
		}
	}
}

@Composable
private fun InfoCard(item: InfoItem) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Box(
				modifier = Modifier
					.size(42.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(Color(0x22FFFFFF)),
				contentAlignment = Alignment.Center
			) {
				Icon(item.icon, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(20.dp))
			}

			Column(modifier = Modifier.weight(1f)) {
				Text(text = item.label, color = GrayText, fontSize = 12.sp)
				Text(
					text = item.value,
					color = WhiteText,
					fontSize = 15.sp,
					fontWeight = FontWeight.SemiBold,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}

@Composable
private fun AccountSection() {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		AccountActionCard(icon = Icons.Filled.Person, title = "Informations du compte", subtitle = "Consulter les informations enregistrées")
		AccountActionCard(icon = Icons.Filled.PhotoCamera, title = "Photo de profil", subtitle = "Aperçu de votre avatar actuel")
		AccountActionCard(icon = Icons.Filled.Lock, title = "Sécurité", subtitle = "Paramètres liés à l’accès au compte")
		AccountActionCard(icon = Icons.Filled.Description, title = "Conditions d’utilisation", subtitle = "Consulter les règles de la plateforme")
		AccountActionCard(icon = Icons.Filled.Logout, title = "Déconnexion", subtitle = "Quitter la session actuelle", accent = Color(0xFFD96B5F))
	}
}

@Composable
private fun AccountActionCard(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	title: String,
	subtitle: String,
	accent: Color = OrangeAccent
) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Box(
				modifier = Modifier
					.size(42.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(accent.copy(alpha = 0.15f)),
				contentAlignment = Alignment.Center
			) {
				Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
			}
			Column(modifier = Modifier.weight(1f)) {
				Text(text = title, color = WhiteText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
				Text(text = subtitle, color = GrayText, fontSize = 12.sp)
			}
			Text(text = "›", color = GrayText, fontSize = 24.sp)
		}
	}
}

@Composable
private fun HistorySummaryCard(reservations: List<ReservationDto>) {
	val total = reservations.size
	val confirmed = reservations.count { it.statut.isConfirmed() }
	val pending = reservations.count { it.statut.isPending() }
	val cancelled = reservations.count { it.statut.isCancelled() }

	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			SummaryRow(label = "Total", value = total.toString(), icon = Icons.Filled.History, tint = BrownPrimary)
			SummaryRow(label = "Confirmées", value = confirmed.toString(), icon = Icons.Filled.CheckCircle, tint = Color(0xFF2E7D32))
			SummaryRow(label = "En attente", value = pending.toString(), icon = Icons.Filled.HourglassTop, tint = OrangeAccent)
			SummaryRow(label = "Annulées", value = cancelled.toString(), icon = Icons.Filled.WarningAmber, tint = Color(0xFFD32F2F))
		}
	}
}

@Composable
private fun SummaryRow(
	label: String,
	value: String,
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	tint: Color
) {
	Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
		Box(
			modifier = Modifier
				.size(36.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(tint.copy(alpha = 0.12f)),
			contentAlignment = Alignment.Center
		) {
			Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(text = label, color = Color(0xFF7A7A7A), fontSize = 12.sp)
			Text(text = value, color = Color(0xFF1F1F1F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
		}
	}
}

@Composable
private fun ReservationHistoryCard(reservation: ReservationDto) {
	val (badgeColor, badgeTextColor, badgeIcon) = reservation.statusStyle()

	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Box(
					modifier = Modifier
						.size(48.dp)
						.clip(RoundedCornerShape(16.dp))
						.background(Color(0x22FFFFFF)),
					contentAlignment = Alignment.Center
				) {
					Icon(Icons.Filled.History, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(22.dp))
				}

				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = reservation.offreTitre.orDash("Réservation"),
						color = WhiteText,
						fontSize = 16.sp,
						fontWeight = FontWeight.Bold,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
					Text(
						text = reservation.dateReservation.orDash("Date non renseignée"),
						color = GrayText,
						fontSize = 12.sp,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				</Column>
			}

			StatusBadge(
				text = reservation.statut.orDash("Statut"),
				backgroundColor = badgeColor,
				textColor = badgeTextColor,
				icon = badgeIcon
			)
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
			.padding(horizontal = 12.dp, vertical = 8.dp)
	) {
		Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
		Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
	}
}

@Composable
private fun SkeletonPersonalInfo() {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		repeat(4) {
			Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
				Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
					Box(
						modifier = Modifier
							.size(42.dp)
							.clip(RoundedCornerShape(14.dp))
							.background(Color(0x22FFFFFF))
						)
					Spacer(modifier = Modifier.width(12.dp))
					Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
						Box(modifier = Modifier.height(12.dp).fillMaxWidth(0.28f).clip(RoundedCornerShape(8.dp)).background(Color(0x33FFFFFF)))
						Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.72f).clip(RoundedCornerShape(8.dp)).background(Color(0x22FFFFFF)))
					}
				}
			}
		}
	}
}

@Composable
private fun ReservationSkeletonList() {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		repeat(3) {
			Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
				Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
					Box(modifier = Modifier.height(18.dp).fillMaxWidth(0.58f).clip(RoundedCornerShape(8.dp)).background(Color(0x33FFFFFF)))
					Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.42f).clip(RoundedCornerShape(8.dp)).background(Color(0x22FFFFFF)))
					Box(modifier = Modifier.height(28.dp).fillMaxWidth(0.34f).clip(RoundedCornerShape(14.dp)).background(Color(0x33FFFFFF)))
				}
			}
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
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Box(
				modifier = Modifier
					.size(42.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(Color(0x26FFFFFF)),
				contentAlignment = Alignment.Center
			) {
				Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = WhiteText)
			}
			Text(text = message, color = WhiteText, modifier = Modifier.weight(1f))
		}
	}
}

@Composable
private fun EmptyStateCard(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	title: String,
	description: String
) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			modifier = Modifier.padding(28.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Box(
				modifier = Modifier
					.size(72.dp)
					.clip(RoundedCornerShape(24.dp))
					.background(Color(0x22FFFFFF)),
				contentAlignment = Alignment.Center
			) {
				Icon(icon, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(28.dp))
			}
			Text(text = title, color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
			Text(text = description, color = GrayText, fontSize = 13.sp, textAlign = TextAlign.Center)
			Button(
				onClick = {},
				shape = RoundedCornerShape(18.dp),
				colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
			) {
				Text(text = "Découvrir les offres")
			}
		}
	}
}

@Composable
private fun InfoCardPlaceholder() {
	Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
		Box(modifier = Modifier.fillMaxWidth().height(72.dp))
	}
}

private data class InfoItem(
	val icon: androidx.compose.ui.graphics.vector.ImageVector,
	val label: String,
	val value: String
)

private fun UserDto?.displayFullName(): String {
	val parts = listOfNotNull(this?.prenom?.takeIf { it.isNotBlank() }, this?.nom?.takeIf { it.isNotBlank() })
	return if (parts.isNotEmpty()) parts.joinToString(" ") else "Aucune information disponible"
}

private fun String?.orDash(fallback: String = "-"): String = this?.takeIf { it.isNotBlank() } ?: fallback

private fun String?.isConfirmed(): Boolean = this?.contains("confirm", ignoreCase = true) == true
private fun String?.isPending(): Boolean = this?.contains("attente", ignoreCase = true) == true || this?.contains("pending", ignoreCase = true) == true
private fun String?.isCancelled(): Boolean = this?.contains("annul", ignoreCase = true) == true

private fun ReservationDto.statusStyle(): Triple<Color, Color, androidx.compose.ui.graphics.vector.ImageVector> {
	return when {
		statut.isConfirmed() -> Triple(Color(0x1A2E7D32), Color(0xFF2E7D32), Icons.Filled.CheckCircle)
		statut.isPending() -> Triple(Color(0x1AFFA000), Color(0xFFF57C00), Icons.Filled.HourglassTop)
		statut.isCancelled() -> Triple(Color(0x1AD32F2F), Color(0xFFD32F2F), Icons.Filled.WarningAmber)
		else -> Triple(Color(0x1A616161), Color(0xFF616161), Icons.Filled.History)
	}
}
