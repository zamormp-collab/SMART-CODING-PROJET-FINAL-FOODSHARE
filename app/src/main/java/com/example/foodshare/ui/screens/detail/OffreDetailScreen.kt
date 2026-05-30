package com.example.foodshare.ui.screens.detail

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.History
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.ui.theme.BrownPrimary
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
	val scrollState = rememberScrollState()

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
			.background(Color(0xFFF4F1ED))
			.safeDrawingPadding()
	) {
		HeaderSection(onBackClick = onBackClick)

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
				AnimatedVisibility(
					visible = loading,
					enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
					exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
				) { LoadingState() }

				AnimatedVisibility(
					visible = !errorMessage.isNullOrBlank(),
					enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
					exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
				) { ErrorStateCard(message = errorMessage.orEmpty()) }

				AnimatedVisibility(
					visible = offer != null && errorMessage.isNullOrBlank() && !loading,
					enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
					exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
				) {
					offer?.let { current ->
						Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
							BannerSection(offer = current)
							MainInfoCard(offer = current)
							DescriptionCard(description = current.description)
							ActionButton(onClick = { /* reserve later */ })
						}
					}
				}
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OffreDetailPreview() {
	FoodShareTheme {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(Color(0xFFF4F1ED))
				.safeDrawingPadding()
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
					BannerSection(
						offer = OffreDto(
							id = "1",
							title = "Double Beef",
							description = "Burger généreux avec frites croustillantes.",
							quantity = 5,
							expirationDate = "15 Juin 2026",
							location = "Port-au-Prince",
							imageUrl = null,
							userId = "u1"
						)
					)
					MainInfoCard(
						offer = OffreDto(
							id = "1",
							title = "Double Beef",
							description = "Burger généreux avec frites croustillantes.",
							quantity = 5,
							expirationDate = "15 Juin 2026",
							location = "Port-au-Prince",
							imageUrl = null,
							userId = "u1"
						)
					)
					DescriptionCard(description = "Burger généreux avec frites croustillantes.")
					ActionButton(onClick = {})
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
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 18.dp),
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
				Text(text = "Détail de l'offre", color = Color(0xFF1F1F1F), fontSize = 24.sp, fontWeight = FontWeight.Bold)
				Text(text = "Consultez les informations de cette offre", color = Color(0xFF7A7A7A), fontSize = 13.sp)
			}

			Box(
				modifier = Modifier
					.size(54.dp)
					.clip(RoundedCornerShape(18.dp))
					.background(Color.White),
				contentAlignment = Alignment.Center
			) {
				Icon(Icons.Filled.Restaurant, contentDescription = null, tint = BrownPrimary)
			}
		}
	}
}

@Composable
private fun BannerSection(offer: OffreDto) {
	Card(
		shape = RoundedCornerShape(26.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		modifier = Modifier
			.fillMaxWidth()
			.height(220.dp)
			.animateContentSize()
	) {
		Box(modifier = Modifier.fillMaxSize()) {
			if (!offer.imageUrl.isNullOrBlank()) {
				AsyncImage(
					model = offer.imageUrl,
					contentDescription = offer.title,
					modifier = Modifier.fillMaxSize(),
					contentScale = ContentScale.Crop
				)
			} else {
				PlaceholderBanner()
			}

			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xAA000000)))),
				contentAlignment = Alignment.BottomStart
			) {
				Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
					BadgeRow(offer = offer)
					Text(text = offer.title ?: "Offre", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
				}
			}
		}
	}
}

@Composable
private fun PlaceholderBanner() {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(Brush.linearGradient(listOf(Color(0xFFEFDCC7), Color(0xFFF8F1E8)))),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
			Icon(Icons.Filled.Image, contentDescription = null, tint = BrownPrimary, modifier = Modifier.size(48.dp))
			Text(text = "Image non disponible", color = Color(0xFF6F4B3E), fontWeight = FontWeight.SemiBold)
		}
	}
}

@Composable
private fun MainInfoCard(offer: OffreDto) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		modifier = Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
			Text(text = offer.title ?: "Offre", color = Color(0xFF1F1F1F), fontSize = 22.sp, fontWeight = FontWeight.Bold)
			InfoRow(icon = Icons.Filled.LocationOn, label = "Localisation", value = offer.location ?: "-", valueColor = Color(0xFF1F1F1F))
			InfoRow(icon = Icons.Filled.Restaurant, label = "Quantité disponible", value = quantityLabel(offer.quantity), valueColor = Color(0xFF1F1F1F))
			InfoRow(icon = Icons.Filled.Event, label = "Date d'expiration", value = offer.expirationDate ?: "-", valueColor = Color(0xFF1F1F1F))
			InfoRow(icon = Icons.Filled.History, label = "Référence", value = offer.id ?: "-", valueColor = Color(0xFF7A7A7A))
		}
	}
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, valueColor: Color) {
	Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
		Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF8F4EE)), contentAlignment = Alignment.Center) {
			Icon(icon, contentDescription = null, tint = BrownPrimary)
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(text = label, color = Color(0xFF7A7A7A), fontSize = 12.sp)
			Text(text = value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
		}
	}
}

@Composable
private fun BadgeRow(offer: OffreDto) {
	Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
		OfferBadge(text = if ((offer.quantity ?: 0) > 0) "Disponible" else "Épuisé", background = Color(0x1AFFFFFF), textColor = WhiteText)
		if ((offer.quantity ?: 0) in 1..3) {
			OfferBadge(text = "Quantité limitée", background = Color(0x1AFFA000), textColor = Color(0xFFFFE0B2))
		}
		if (isExpirationClose(offer.expirationDate)) {
			OfferBadge(text = "Expiration proche", background = Color(0x1AD32F2F), textColor = Color(0xFFFFCDD2))
		}
	}
}

@Composable
private fun OfferBadge(text: String, background: Color, textColor: Color) {
	Box(
		modifier = Modifier
			.clip(RoundedCornerShape(999.dp))
			.background(background)
			.padding(horizontal = 12.dp, vertical = 8.dp)
	) {
		Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
	}
}

@Composable
private fun DescriptionCard(description: String?) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
			Text(text = "Description", color = WhiteText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
			Text(text = description?.takeIf { it.isNotBlank() } ?: "Aucune description disponible", color = WhiteText, fontSize = 13.sp, lineHeight = 19.sp)
		}
	}
}

@Composable
private fun ActionButton(onClick: () -> Unit) {
	Button(
		onClick = onClick,
		colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
		shape = RoundedCornerShape(22.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Icon(Icons.Filled.AddShoppingCart, contentDescription = null, tint = WhiteText)
		Spacer(modifier = Modifier.width(8.dp))
		Text(text = "Réserver maintenant", color = WhiteText, fontWeight = FontWeight.Bold)
	}
}

@Composable
private fun LoadingState() {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		SkeletonBanner()
		SkeletonCard(height = 180.dp)
		SkeletonCard(height = 130.dp)
		SkeletonCard(height = 110.dp)
	}
}

@Composable
private fun SkeletonBanner() {
	Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().height(220.dp)) {
		Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2E8DE)))
	}
}

@Composable
private fun SkeletonCard(height: androidx.compose.ui.unit.Dp) {
	Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.fillMaxWidth().height(height)) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Box(modifier = Modifier.fillMaxWidth(0.55f).height(18.dp).background(Color(0x33FFFFFF)))
			Box(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp).background(Color(0x22FFFFFF)))
			Box(modifier = Modifier.fillMaxWidth(0.7f).height(12.dp).background(Color(0x22FFFFFF)))
			Box(modifier = Modifier.fillMaxWidth(0.4f).height(42.dp).background(Color(0x33FFFFFF)))
		}
	}
}

@Composable
private fun ErrorStateCard(message: String) {
	Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF4A2320)), modifier = Modifier.fillMaxWidth()) {
		Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Color(0x26FFFFFF)), contentAlignment = Alignment.Center) {
				Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = WhiteText)
			}
			Text(text = message, color = WhiteText, modifier = Modifier.weight(1f))
		}
	}
}

private fun quantityLabel(quantity: Int?): String = when {
	quantity == null -> "-"
	quantity <= 1 -> "$quantity portion"
	else -> "$quantity portions"
}

private fun isExpirationClose(expirationDate: String?): Boolean {
	val text = expirationDate?.lowercase().orEmpty()
	return text.contains("jourd") || text.contains("today") || text.contains("expir")
}
