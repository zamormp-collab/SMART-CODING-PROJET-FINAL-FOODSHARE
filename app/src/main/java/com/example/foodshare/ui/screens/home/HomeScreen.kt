package com.example.foodshare.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.ui.theme.BrownPrimary
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

@Composable
fun HomeScreen(
    onOfferClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val userViewModel: UserViewModel = viewModel(factory = remember(sessionManager) { UserViewModelFactory(sessionManager) })
    val homeViewModel: HomeViewModel = viewModel(factory = remember { HomeViewModelFactory() })

    val cachedUserState = remember { userViewModel.getCachedUser() }
    val userUiState = userViewModel.uiState
    val homeUiState = homeViewModel.uiState

    LaunchedEffect(Unit) {
        if (cachedUserState !is UserState.Success && userUiState is UserState.Idle) {
            userViewModel.loadCurrentUser()
        }
        if (homeUiState is HomeState.Idle) {
            homeViewModel.loadHomeData(
                userId = (cachedUserState as? UserState.Success)?.user?.id
                    ?: (userUiState as? UserState.Success)?.user?.id
            )
        }
    }

    val user: UserDto? = when {
        userUiState is UserState.Success -> userUiState.user
        cachedUserState is UserState.Success -> cachedUserState.user
        else -> null
    }

    val loading = userUiState is UserState.Loading || homeUiState is HomeState.Loading
    val errorMessage = (userUiState as? UserState.Error)?.message ?: (homeUiState as? HomeState.Error)?.message
    val offers = (homeUiState as? HomeState.Success)?.offers.orEmpty()

    HomeScreenContent(
        user = user,
        offers = offers,
        loading = loading,
        errorMessage = errorMessage,
        onOfferClick = onOfferClick,
        onProfileClick = onProfileClick
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    FoodShareTheme {
        HomeScreenContent(
            user = UserDto(
                id = "1",
                nom = "Patrick",
                prenom = "",
                email = "patrick@example.com",
                role = "Utilisateur",
                avatarUrl = null
            ),
            offers = listOf(
                OffreDto(
                    id = "1",
                    title = "Double Beef",
                    description = "Burger maison généreux avec sauce spéciale.",
                    quantity = 1,
                    expirationDate = "01/06/2026",
                    location = "Paris",
                    imageUrl = null,
                    userId = "u1"
                ),
                OffreDto(
                    id = "2",
                    title = "Chicken Box",
                    description = "Poulet croustillant + boisson incluse.",
                    quantity = 2,
                    expirationDate = "02/06/2026",
                    location = "Lyon",
                    imageUrl = null,
                    userId = "u2"
                )
            ),
            loading = false,
            errorMessage = null,
            onOfferClick = {},
            onProfileClick = {}
        )
    }
}

@Composable
private fun HomeScreenContent(
    user: UserDto?,
    offers: List<OffreDto>,
    loading: Boolean,
    errorMessage: String?,
    onOfferClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredOffers by remember(offers, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) offers else offers.filter {
                listOfNotNull(it.title, it.description, it.location)
                    .joinToString(" ")
                    .contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F1ED))
    ) {
        HomeHeaderSection(user = user, onProfileClick = onProfileClick)

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SearchBarLikeCard(query = searchQuery, onQueryChange = { searchQuery = it })
                SectionTitle()

                when {
                    loading -> LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE6A15A),
                        trackColor = Color(0x33FFFFFF)
                    )
                    !errorMessage.isNullOrBlank() -> ErrorStateCard(message = errorMessage)
                    filteredOffers.isEmpty() -> EmptyStateCard(
                        message = if (searchQuery.isBlank()) {
                            "Aucune offre disponible pour le moment"
                        } else {
                            "Aucune offre ne correspond à votre recherche"
                        }
                    )
                    else -> filteredOffers.forEach { offer ->
                        val uiOffer = offer.toCardUi()
                        FoodCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = uiOffer.title,
                            description = uiOffer.description,
                            badge = uiOffer.badge,
                            icon = uiOffer.icon,
                            onClick = { offer.id?.let(onOfferClick) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderSection(user: UserDto?, onProfileClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onProfileClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bonjour ${user?.nom ?: "Utilisateur"}",
                        color = Color(0xFF1F1F1F),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user?.role?.let {
                            "${it.lowercase().replaceFirstChar { c -> c.uppercase() }} • ${user.email}"
                        } ?: "Appuyez pour voir votre profil",
                        color = GrayText,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = Color(0xFF222222)
                )
            }
        }
    }
}

@Composable
private fun SearchBarLikeCard(query: String, onQueryChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F4F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
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
private fun ErrorStateCard(message: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A2320)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = message, color = Color.White, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun SectionTitle() {
    Text(
        text = "Offres",
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
                        .background(Color(0xFF3E2518)),
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
