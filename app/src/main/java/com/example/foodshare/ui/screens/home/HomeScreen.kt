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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.ui.theme.GrayText
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText
import com.example.foodshare.viewmodel.HomeState
import com.example.foodshare.viewmodel.HomeViewModel
import com.example.foodshare.viewmodel.HomeViewModelFactory
import com.example.foodshare.viewmodel.UserState
import com.example.foodshare.viewmodel.UserViewModel
import com.example.foodshare.viewmodel.UserViewModelFactory

private data class HomeOfferCardUi(
    val title: String,
    val description: String,
    val badge: String,
    val icon: ImageVector
)

private data class HomeReservationCardUi(
    val serviceName: String,
    val reservationDate: String,
    val status: String
)

@Composable
fun HomeScreen(
    onOfferClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val factory: ViewModelProvider.Factory = remember(sessionManager) { UserViewModelFactory(sessionManager) }
    val userViewModel: UserViewModel = viewModel<UserViewModel>(factory = factory)
    val homeFactory: ViewModelProvider.Factory = remember(sessionManager) { HomeViewModelFactory(sessionManager) }
    val homeViewModel: HomeViewModel = viewModel<HomeViewModel>(factory = homeFactory)

    val cachedUserState = remember { userViewModel.getCachedUser() }
    val userUiState = userViewModel.uiState
    val homeUiState = homeViewModel.uiState

    LaunchedEffect(Unit) {
        if (cachedUserState !is UserState.Success && userUiState is UserState.Idle) {
            userViewModel.loadCurrentUser()
        }
        if (homeUiState is HomeState.Idle) {
            homeViewModel.loadHomeData()
        }
    }

    val user: UserDto? = when {
        userUiState is UserState.Success -> userUiState.user
        cachedUserState is UserState.Success -> cachedUserState.user
        else -> null
    }

    val loading = userUiState is UserState.Loading || homeUiState is HomeState.Loading
    val errorMessage = (userUiState as? UserState.Error)?.message ?: (homeUiState as? HomeState.Error)?.message

    var searchQuery by remember { mutableStateOf("") }
    val offers = (homeUiState as? HomeState.Success)?.offers.orEmpty()
    val filteredOffers by remember(offers, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) offers else offers.filter {
                listOfNotNull(it.title, it.description, it.location)
                    .joinToString(" ")
                    .contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrownPrimary,
                        DarkBackground
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
            HomeTopHeader(user = user, onProfileClick = onProfileClick)

            Spacer(modifier = Modifier.height(14.dp))

            SearchBarLikeCard(query = searchQuery, onQueryChange = { searchQuery = it })

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle(title = "Offres")

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredOffers.isEmpty()) {
                EmptyStateCard(
                    message = if (searchQuery.isBlank()) {
                        "Aucune offre disponible pour le moment"
                    } else {
                        "Aucune offre ne correspond à votre recherche"
                    }
                )
            } else {
                filteredOffers.forEach { offer ->
                    val uiOffer = offer.toCardUi()
                    FoodCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = uiOffer.title,
                        description = uiOffer.description,
                        badge = uiOffer.badge,
                        icon = uiOffer.icon,
                        onClick = { offer.id?.let(onOfferClick) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    FoodShareTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF5A3726), Color(0xFF1A120F))
                    )
                )
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                HomeTopHeader(
                    user = UserDto(id = "1", nom = "Patrick", prenom = "", email = "patrick@example.com", role = "Utilisateur", avatarUrl = null),
                    onProfileClick = {}
                )

                Spacer(modifier = Modifier.height(14.dp))
                SearchBarLikeCard(query = "", onQueryChange = {})
                Spacer(modifier = Modifier.height(18.dp))
                SectionTitle(title = "Offres")
                Spacer(modifier = Modifier.height(10.dp))

                FoodCard(modifier = Modifier.fillMaxWidth(), title = "Double Beef", description = "Burger maison", badge = "1 portion", icon = Icons.Default.ShoppingCart, onClick = {})
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private fun OffreDto.toCardUi(): HomeOfferCardUi {
    val titleText = title?.takeIf { it.isNotBlank() } ?: "Offre"
    val descriptionText = description?.takeIf { it.isNotBlank() } ?: "Aucune description disponible"
    val badgeText = quantity?.let { "$it portion(s)" } ?: "Disponible"
    val icon = when (titleText.lowercase()) {
        "fish fillet" -> Icons.Default.FavoriteBorder
        "chicken crisp" -> Icons.Default.NotificationsNone
        else -> Icons.Default.ShoppingCart
    }
    return HomeOfferCardUi(titleText, descriptionText, badgeText, icon)
}

private fun ReservationDto.toCardUi(): HomeReservationCardUi {
    return HomeReservationCardUi(
        serviceName = offreTitre?.takeIf { it.isNotBlank() } ?: "Réservation",
        reservationDate = dateReservation?.takeIf { it.isNotBlank() } ?: "Date non renseignée",
        status = statut?.takeIf { it.isNotBlank() } ?: "En attente"
    )
}

@Composable
private fun HomeTopHeader(user: UserDto?, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrownPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.nom?.firstOrNull()?.uppercase() ?: "F",
                    color = WhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = "Bonjour ${user?.nom ?: "Utilisateur"}", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = user?.role?.let {
                        "${it.lowercase().replaceFirstChar { c -> c.uppercase() }} • ${user.email}"
                    } ?: "Que souhaitez-vous manger aujourd'hui ?",
                    color = GrayText,
                    fontSize = 13.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = WhiteText
        )
    }
}

@Composable
private fun SearchBarLikeCard(query: String, onQueryChange: (String) -> Unit) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material3.OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Rechercher une offre") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Rechercher", tint = GrayText) },
            singleLine = true,
            shape = RoundedCornerShape(22.dp)
        )
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = message, color = WhiteText, modifier = Modifier.padding(16.dp))
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
                    .background(if (selected) OrangeAccent else Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = if (selected) WhiteText else Color(0xFF333333),
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
        color = WhiteText,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FoodCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    badge: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(190.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
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
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = OrangeAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = GrayText,
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
                    text = badge,
                    fontSize = 14.sp,
                    color = WhiteText,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(OrangeAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


