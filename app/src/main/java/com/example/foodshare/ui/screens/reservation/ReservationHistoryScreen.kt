package com.example.foodshare.ui.screens.reservation

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText

data class ReservationHistoryItem(
	val title: String,
	val date: String,
	val status: String,
	val note: String,
	val accent: Color,
	val imageUrl: String? = null
)

@Composable
fun ReservationHistoryScreen(
	onBackClick: () -> Unit = {},
	onReservationClick: (String) -> Unit = {},
	items: List<ReservationHistoryItem> = defaultHistoryItems(),
	loading: Boolean = false,
	errorMessage: String? = null
) {
	val summary = remember(items) {
		HistorySummary(
			total = items.size,
			confirmed = items.count { it.status.contains("confirm", ignoreCase = true) },
			pending = items.count { it.status.contains("attente", ignoreCase = true) || it.status.contains("pending", ignoreCase = true) },
			cancelled = items.count { it.status.contains("annul", ignoreCase = true) }
		)
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(Color(0xFFF8F4EE))
	) {
		HistoryHeader(
			onBackClick = onBackClick,
			summary = summary
		)

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
				SectionTitle(title = "Historique")

				FilterChipsRow()

				when {
					loading -> LoadingSkeletonList()
					!errorMessage.isNullOrBlank() -> ErrorStateCard(message = errorMessage)
					items.isEmpty() -> EmptyHistoryState()
					else -> items.forEachIndexed { index, item ->
						HistoryItemCard(
							item = item,
							onClick = { onReservationClick(index.toString()) }
						)
					}
				}

				Spacer(modifier = Modifier.height(8.dp))
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReservationHistoryScreenPreview() {
	FoodShareTheme {
		ReservationHistoryScreen(
			items = listOf(
				ReservationHistoryItem(
					title = "Double Beef Burger",
					date = "30/05/2026 • 12:30",
					status = "Confirmée",
					note = "Réservation validée et récupérée avec succès. Merci de votre achat !",
					accent = Color(0xFF2E7D32)
				),
				ReservationHistoryItem(
					title = "Chicken Crisp Wrap",
					date = "29/05/2026 • 18:00",
					status = "En attente",
					note = "Votre demande est en cours de traitement. Nous vous confirmerons sous peu.",
					accent = OrangeAccent
				),
				ReservationHistoryItem(
					title = "Fresh Salad Bowl",
					date = "27/05/2026 • 13:15",
					status = "Annulée",
					note = "La réservation a été annulée avant la récupération.",
					accent = Color(0xFFD32F2F)
				),
				ReservationHistoryItem(
					title = "Vegetarian Pasta Box",
					date = "26/05/2026 • 11:45",
					status = "Confirmée",
					note = "Repas végétarien bio - Tous les ingrédients sont frais de ce matin.",
					accent = Color(0xFF2E7D32)
				),
				ReservationHistoryItem(
					title = "Dessert Chocolate Cake",
					date = "25/05/2026 • 16:00",
					status = "Confirmée",
					note = "Gâteau au chocolat maison - Récupération avant 17h30.",
					accent = Color(0xFF2E7D32)
				)
			),
			loading = false,
			errorMessage = null
		)
	}
}

@Composable
private fun HistoryHeader(
	onBackClick: () -> Unit,
	summary: HistorySummary
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(Color(0xFFF8F4EE))
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
					text = "Historique des réservations",
					color = Color(0xFF1F1F1F),
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = "Retrouvez toutes vos réservations passées",
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

		SummaryCard(summary = summary)
	}
}

@Composable
private fun SummaryCard(summary: HistorySummary) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		modifier = Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			HistoryStatRow(
				label = "Total",
				value = summary.total.toString(),
				icon = Icons.Filled.History,
				tint = BrownPrimary
			)
			HistoryStatRow(
				label = "Confirmées",
				value = summary.confirmed.toString(),
				icon = Icons.Filled.CheckCircle,
				tint = Color(0xFF2E7D32)
			)
			HistoryStatRow(
				label = "En attente",
				value = summary.pending.toString(),
				icon = Icons.Filled.HourglassTop,
				tint = OrangeAccent
			)
			HistoryStatRow(
				label = "Annulées",
				value = summary.cancelled.toString(),
				icon = Icons.Filled.ErrorOutline,
				tint = Color(0xFFD32F2F)
			)
		}
	}
}

@Composable
private fun HistoryStatRow(
	label: String,
	value: String,
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	tint: Color
) {
	Card(
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7F4)),
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier.padding(14.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Box(
				modifier = Modifier
					.size(34.dp)
					.clip(RoundedCornerShape(12.dp))
					.background(tint.copy(alpha = 0.12f)),
				contentAlignment = Alignment.Center
			) {
				Icon(imageVector = icon, contentDescription = null, tint = tint)
			}

			Column(modifier = Modifier.weight(1f)) {
				Text(text = label, color = Color(0xFF7A7A7A), fontSize = 12.sp)
				Text(text = value, color = Color(0xFF1F1F1F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
private fun FilterChipsRow() {
	Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
		listOf("Tous", "Confirmées", "En attente", "Annulées").forEachIndexed { index, label ->
			val selected = index == 0
			Box(
				modifier = Modifier
					.clip(RoundedCornerShape(20.dp))
					.background(if (selected) OrangeAccent else Color.White)
					.padding(horizontal = 16.dp, vertical = 9.dp)
			) {
				Text(
					text = label,
					color = if (selected) WhiteText else Color(0xFF333333),
					fontSize = 13.sp,
					fontWeight = FontWeight.SemiBold
				)
			}
		}
	}
}

@Composable
private fun HistoryItemCard(
	item: ReservationHistoryItem,
	onClick: () -> Unit
) {
	Card(
		onClick = onClick,
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		shape = RoundedCornerShape(24.dp),
		modifier = Modifier.fillMaxWidth(),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Column(
			modifier = Modifier.padding(0.dp),
			verticalArrangement = Arrangement.spacedBy(0.dp)
		) {
			// Image banner
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(120.dp)
					.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
					.background(LinearGradient(
						colors = listOf(
							item.accent.copy(alpha = 0.3f),
							item.accent.copy(alpha = 0.15f)
						)
					)),
				contentAlignment = Alignment.Center
			) {
				if (!item.imageUrl.isNullOrBlank()) {
					// Image placeholder (real implementation would use Coil)
					Box(
						modifier = Modifier
							.fillMaxSize()
							.background(item.accent.copy(alpha = 0.2f))
					)
				} else {
					Icon(
						Icons.Filled.History,
						contentDescription = null,
						tint = item.accent.copy(alpha = 0.8f),
						modifier = Modifier.size(42.dp)
					)
				}
			}

			// Content
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					Box(
						modifier = Modifier
							.size(40.dp)
							.clip(CircleShape)
							.background(item.accent.copy(alpha = 0.18f)),
						contentAlignment = Alignment.Center
					) {
						Icon(Icons.Filled.History, contentDescription = null, tint = item.accent, modifier = Modifier.size(18.dp))
					}

					Column(modifier = Modifier.weight(1f)) {
						Text(
							text = item.title,
							color = WhiteText,
							fontSize = 16.sp,
							fontWeight = FontWeight.Bold,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
						Text(
							text = item.date,
							color = Color(0xFFBDBDBD),
							fontSize = 12.sp,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
					}
				}

				StatusBadge(status = item.status, tint = item.accent)

				Text(
					text = item.note,
					color = Color(0xFFE0E0E0),
					fontSize = 12.sp,
					lineHeight = 16.sp,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)

				Button(
					onClick = onClick,
					colors = ButtonDefaults.buttonColors(containerColor = item.accent),
					shape = RoundedCornerShape(14.dp),
					modifier = Modifier
						.fillMaxWidth()
						.height(38.dp)
				) {
					Text(text = "Voir le détail", fontSize = 13.sp)
				}
			}
		}
	}
}

// Helper gradient function (simplified)
private fun LinearGradient(colors: List<Color>): Color = colors.first()

@Composable
private fun StatusBadge(status: String, tint: Color) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier
			.clip(RoundedCornerShape(999.dp))
			.background(tint.copy(alpha = 0.14f))
			.padding(horizontal = 12.dp, vertical = 8.dp)
	) {
		Box(
			modifier = Modifier
				.size(8.dp)
				.clip(CircleShape)
				.background(tint)
		)
		Text(
			text = status,
			color = tint,
			fontSize = 12.sp,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
private fun LoadingSkeletonList() {
	Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
		repeat(3) {
			SkeletonHistoryCard()
		}
	}
}

@Composable
private fun SkeletonHistoryCard() {
	Card(
		colors = CardDefaults.cardColors(containerColor = DarkSurface),
		shape = RoundedCornerShape(24.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
			Box(
				modifier = Modifier
					.height(18.dp)
					.fillMaxWidth(0.58f)
					.clip(RoundedCornerShape(8.dp))
					.background(Color(0x33FFFFFF))
			)
			Box(
				modifier = Modifier
					.height(14.dp)
					.fillMaxWidth(0.42f)
					.clip(RoundedCornerShape(8.dp))
					.background(Color(0x22FFFFFF))
			)
			Box(
				modifier = Modifier
					.height(28.dp)
					.fillMaxWidth(0.32f)
					.clip(RoundedCornerShape(14.dp))
					.background(Color(0x33FFFFFF))
			)
			Box(
				modifier = Modifier
					.height(42.dp)
					.fillMaxWidth(0.85f)
					.clip(RoundedCornerShape(16.dp))
					.background(Color(0x22FFFFFF))
			)
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

			Text(
				text = message,
				color = WhiteText,
				modifier = Modifier.weight(1f)
			)
		}
	}
}

@Composable
private fun EmptyHistoryState() {
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
				Icon(Icons.Filled.History, contentDescription = null, tint = OrangeAccent)
			}

			Text(
				text = "Aucun historique disponible",
				color = WhiteText,
				fontSize = 18.sp,
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Center
			)
			Text(
				text = "Vos réservations passées apparaîtront ici",
				color = Color(0xFFBDBDBD),
				fontSize = 13.sp,
				textAlign = TextAlign.Center
			)

			Button(
				onClick = {},
				colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
				shape = RoundedCornerShape(18.dp)
			) {
				Text(text = "Découvrir les offres")
			}
		}
	}
}

private data class HistorySummary(
	val total: Int,
	val confirmed: Int,
	val pending: Int,
	val cancelled: Int
)

private fun defaultHistoryItems(): List<ReservationHistoryItem> = listOf(
	ReservationHistoryItem(
		title = "Double Beef",
		date = "30/05/2026 • 12:30",
		status = "Confirmée",
		note = "Réservation validée et récupérée avec succès.",
		accent = Color(0xFF2E7D32)
	),
	ReservationHistoryItem(
		title = "Chicken Crisp",
		date = "29/05/2026 • 18:00",
		status = "En attente",
		note = "Votre demande est en cours de traitement.",
		accent = OrangeAccent
	),
	ReservationHistoryItem(
		title = "Fresh Salad",
		date = "27/05/2026 • 13:15",
		status = "Annulée",
		note = "La réservation a été annulée avant la récupération.",
		accent = Color(0xFFD32F2F)
	)
)
