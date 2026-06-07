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
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.foodshare.ui.theme.WhiteText
import com.example.foodshare.viewmodel.HomeState
import com.example.foodshare.viewmodel.HomeViewModel
import com.example.foodshare.viewmodel.HomeViewModelFactory
import com.example.foodshare.viewmodel.UserState
import com.example.foodshare.viewmodel.UserViewModel
import com.example.foodshare.viewmodel.UserViewModelFactory

@Composable
fun HomeScreen(
    onOfferClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val userViewModel: UserViewModel = viewModel(factory = remember(sessionManager) { UserViewModelFactory(sessionManager) })
    val homeViewModel: HomeViewModel = viewModel(factory = remember(sessionManager) { HomeViewModelFactory(sessionManager) })

    val cachedUserState = remember { userViewModel.getCachedUser() }
    val userUiState = userViewModel.uiState
    val homeUiState = homeViewModel.uiState

    LaunchedEffect(Unit) {
        if (cachedUserState !is UserState.Success && userUiState is UserState.Idle) {
            userViewModel.loadCurrentUser()
        }
        
        val userId = (cachedUserState as? UserState.Success)?.user?.id
            ?: (userUiState as? UserState.Success)?.user?.id
            
        if (homeUiState is HomeState.Idle) {
            homeViewModel.loadHomeData(userId)
        }
        
        homeViewModel.startAutoRefresh(userId)
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
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
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
                        OffreCard(
                            offre = offer,
                            onClick = { 
                                // On s'assure que l'ID n'est pas nul avant de naviguer
                                offer.id?.let { id -> onOfferClick(id) }
                            },
                            modifier = Modifier.fillMaxWidth()
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
