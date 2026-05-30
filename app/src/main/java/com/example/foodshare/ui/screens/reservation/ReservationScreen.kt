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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.DeleteOutline
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
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.repository.OffreRepository
import com.example.foodshare.data.repository.ReservationRepository
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText
import kotlinx.coroutines.launch

@Composable
fun ReservationScreen(
    onBackClick: () -> Unit = {},
    onReservationClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
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

    val confirmedCount = reservations.count { it.statut.isConfirmed() }
    val pendingCount = reservations.count { it.statut.isPending() }
    val totalCount = reservations.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EC))
    ) {
        ReservationHeader(
            onBackClick = onBackClick,
            totalCount = totalCount,
            confirmedCount = confirmedCount,
            pendingCount = pendingCount
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
                SectionTitle(title = "Réservations")

                when {
                    loading -> LoadingSkeletonList()
                    !errorMessage.isNullOrBlank() -> ErrorStateCard(message = errorMessage.orEmpty())
                    reservations.isEmpty() -> EmptyReservationsState()
                    else -> {
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

                if (offers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionTitle(title = "Offres disponibles")
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
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReservationScreenPreview() {
    FoodShareTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            ReservationHeader(onBackClick = {}, totalCount = 8, confirmedCount = 5, pendingCount = 2)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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
                    SectionTitle(title = "Réservations")
                    ReservationItemCard(
                        reservation = ReservationDto(
                            id = "r1",
                            offreId = "1",
                            offreTitre = "Preview Burger",
                            dateReservation = "30/05/2026 12:00",
                            statut = "Confirmée"
                        ),
                        onClick = {},
                        onCancel = {}
                    )
                    ReservationItemCard(
                        reservation = ReservationDto(
                            id = "r2",
                            offreId = "2",
                            offreTitre = "Chicken Box",
                            dateReservation = "30/05/2026 13:00",
                            statut = "En attente"
                        ),
                        onClick = {},
                        onCancel = {}
                    )
                    ReservationItemCard(
                        reservation = ReservationDto(
                            id = "r3",
                            offreId = "3",
                            offreTitre = "Fresh Salad",
                            dateReservation = "29/05/2026 19:30",
                            statut = "Annulée"
                        ),
                        onClick = {},
                        onCancel = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservationHeader(
    onBackClick: () -> Unit,
    totalCount: Int,
    confirmedCount: Int,
    pendingCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
            .background(Color(0xFFF8F4EE))
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color(0xFF222222)
                )
            }

            Column(modifier = Modifier.fillMaxWidth(0.78f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Mes Réservations",
                    color = Color(0xFF1F1F1F),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Suivez et gérez toutes vos réservations",
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
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = BrownPrimary
                )
            }
        }

        ReservationStatsCard(
            totalCount = totalCount,
            confirmedCount = confirmedCount,
            pendingCount = pendingCount
        )
    }
}

@Composable
private fun ReservationStatsCard(
    totalCount: Int,
    confirmedCount: Int,
    pendingCount: Int
) {
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
            StatChip(
                modifier = Modifier.fillMaxWidth(),
                label = "Total",
                value = totalCount.toString(),
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                tint = BrownPrimary
            )
            StatChip(
                modifier = Modifier.fillMaxWidth(),
                label = "Confirmées",
                value = confirmedCount.toString(),
                icon = Icons.Filled.CheckCircle,
                tint = Color(0xFF2E7D32)
            )
            StatChip(
                modifier = Modifier.fillMaxWidth(),
                label = "En attente",
                value = pendingCount.toString(),
                icon = Icons.Filled.HourglassTop,
                tint = OrangeAccent
            )
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7F4))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
            Text(text = value, color = Color(0xFF1F1F1F), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = Color(0xFF7A7A7A), fontSize = 12.sp)
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
private fun LoadingSkeletonList() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(3) {
            SkeletonReservationCard()
        }
    }
}

@Composable
private fun SkeletonReservationCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth(0.55f)
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(76.dp, 28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x22FFFFFF))
                )
                Box(
                    modifier = Modifier
                        .size(96.dp, 28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x22FFFFFF))
                )
            }
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(0.34f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33FFFFFF))
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
                Text(text = message, color = WhiteText, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun EmptyReservationsState() {
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
                text = "Aucune réservation pour le moment",
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Vos futures réservations apparaîtront ici",
                color = Color(0xFFBDBDBD),
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun OfferReservationCard(offer: OffreDto, onReserve: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Restaurant, contentDescription = null, tint = OrangeAccent)
                }
                Column(modifier = Modifier.fillMaxWidth(0.74f)) {
                    Text(text = offer.title ?: "Offre", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = offer.location?.let { "Lieu : $it" } ?: "Lieu non renseigné",
                        color = Color(0xFFBDBDBD),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(text = offer.description ?: "Aucune description", color = WhiteText, fontSize = 13.sp)
            Button(
                onClick = onReserve,
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "Réserver")
            }
        }
    }
}

@Composable
private fun ReservationItemCard(
    reservation: ReservationDto,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    val (badgeBg, badgeTextColor, badgeIcon) = statusStyle(reservation.statut)
    val canCancel = !reservation.statut.isCancelled()

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    Icon(Icons.Filled.Restaurant, contentDescription = null, tint = OrangeAccent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reservation.offreTitre ?: "Réservation",
                        color = WhiteText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = reservation.dateReservation ?: "Date non renseignée",
                        color = Color(0xFFBDBDBD),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            StatusBadge(
                text = reservation.statut ?: "Statut",
                backgroundColor = badgeBg,
                textColor = badgeTextColor,
                icon = badgeIcon
            )

            if (canCancel) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB84A4A)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = WhiteText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Annuler")
                }
            }
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
