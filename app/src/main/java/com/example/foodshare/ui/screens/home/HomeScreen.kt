package com.example.foodshare.ui.screens.home

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.viewmodel.UserState
import com.example.foodshare.viewmodel.UserViewModel
import com.example.foodshare.viewmodel.UserViewModelFactory

private data class HomeOffer(
    val title: String,
    val description: String,
    val price: String,
    val icon: ImageVector
)

private data class HomeReservation(
    val serviceName: String,
    val reservationDate: String,
    val status: String
)

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val factory: ViewModelProvider.Factory = remember(sessionManager) { UserViewModelFactory(sessionManager) }
    val userViewModel: UserViewModel = viewModel<UserViewModel>(factory = factory)

    val cachedUserState = remember { userViewModel.getCachedUser() }
    val uiState = userViewModel.uiState

    LaunchedEffect(Unit) {
        if (cachedUserState !is UserState.Success && uiState is UserState.Idle) {
            userViewModel.loadCurrentUser()
        }
    }

    val user: UserDto? = when {
        uiState is UserState.Success -> uiState.user
        cachedUserState is UserState.Success -> cachedUserState.user
        else -> null
    }

    val loading = uiState is UserState.Loading
    val errorMessage = (uiState as? UserState.Error)?.message

    val categories = remember { listOf("Burger", "Pizza", "Végétarien") }
    val offers = remember {
        listOf(
            HomeOffer("Double Beef", "Burger maison avec viande double et fromage fondant", "12", Icons.Default.ShoppingCart),
            HomeOffer("Single Beef", "Simple et savoureux, parfait pour une petite faim", "9", Icons.Default.ShoppingCart),
            HomeOffer("Fish Fillet", "Poisson croustillant avec sauce légère", "12", Icons.Default.FavoriteBorder),
            HomeOffer("Chicken Crisp", "Poulet croustillant et pommes de terre", "12", Icons.Default.NotificationsNone)
        )
    }
    val reservations = remember {
        listOf(
            HomeReservation("Double Beef", "Aujourd'hui - 12:30", "Confirmée"),
            HomeReservation("Chicken Crisp", "Demain - 18:00", "En attente"),
            HomeReservation("Fish Fillet", "27 Mai - 13:00", "Récupérée")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5A3726),
                        Color(0xFF1A120F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            HomeTopHeader(user = user)

            Spacer(modifier = Modifier.height(14.dp))

            SearchBarLikeCard()

            Spacer(modifier = Modifier.height(14.dp))

            CategoryRow(categories = categories)

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle(title = "Offers")

            Spacer(modifier = Modifier.height(10.dp))

            offers.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { offer ->
                        FoodCard(
                            modifier = Modifier.weight(1f),
                            title = offer.title,
                            description = offer.description,
                            price = offer.price,
                            icon = offer.icon
                        )
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionTitle(title = "Reservations")

            Spacer(modifier = Modifier.height(10.dp))

            reservations.forEach { reservation ->
                ReservationCard(
                    serviceName = reservation.serviceName,
                    reservationDate = reservation.reservationDate,
                    status = reservation.status
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (loading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE6A15A),
                    trackColor = Color(0x33222222)
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4A2320)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.White,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun HomeTopHeader(user: UserDto?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF6B4531)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.nom?.firstOrNull()?.uppercase() ?: "F",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Hello, ${user?.nom ?: "Faizah"}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user?.role?.let {
                        "${it.lowercase().replaceFirstChar { c -> c.uppercase() }} • ${user.email}"
                    } ?: "What do you want to eat today?",
                    color = Color(0xFFD9D2CC),
                    fontSize = 13.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = Color.White
        )
    }
}

@Composable
private fun SearchBarLikeCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF9E9E9E)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Search for food",
                color = Color(0xFF9E9E9E),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CategoryRow(categories: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        categories.forEachIndexed { index, category ->
            val selected = index == 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) Color(0xFFE67E22) else Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = if (selected) Color.White else Color(0xFF333333),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FoodCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    price: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier.height(190.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF20140F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFFE6A15A),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color(0xFFBDBDBD),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "€ $price",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE6A15A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(serviceName: String, reservationDate: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A13))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = serviceName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reservationDate,
                        color = Color(0xFFDDCFC6),
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            when (status) {
                                "Confirmée" -> Color(0xFF2E7D32)
                                "Récupérée" -> Color(0xFF1565C0)
                                else -> Color(0xFFE6A15A)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = status,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Réservation active",
                    color = Color(0xFFDDCFC6),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )

                Text(
                    text = "Voir",
                    color = Color(0xFFE6A15A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

