package com.example.foodshare.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.foodshare.data.remote.dto.OffreDto
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.GrayText
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun OffreCard(
    offre: OffreDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiOffer = offre.toCardUi()
    
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!uiOffer.imageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = uiOffer.imageUrl,
                    contentDescription = uiOffer.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 14.dp, bottomEnd = 14.dp)),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF3E2518)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangeAccent, strokeWidth = 2.dp)
                        }
                    },
                    error = {
                        OfferImageFallback(title = uiOffer.title, icon = uiOffer.icon)
                    }
                )
            } else {
                OfferImageFallback(title = uiOffer.title, icon = uiOffer.icon)
            }

            Text(
                text = uiOffer.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteText
            )

            Text(
                text = uiOffer.description,
                fontSize = 11.sp,
                color = GrayText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiOffer.badge,
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

@Composable
private fun OfferImageFallback(title: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
            .background(Color(0xFF3E2518)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (icon == Icons.Default.ShoppingCart) Icons.Default.Restaurant else icon,
            contentDescription = title,
            tint = OrangeAccent,
            modifier = Modifier.size(40.dp)
        )
    }
}

private data class HomeOfferCardUi(
    val title: String,
    val description: String,
    val badge: String,
    val imageUrl: String?,
    val icon: ImageVector
)

private fun OffreDto.toCardUi(): HomeOfferCardUi {
    val titleText = title?.takeIf { it.isNotBlank() } ?: "Offre"
    val descriptionText = description?.takeIf { it.isNotBlank() } ?: "Aucune description disponible"
    
    // Correction de l'affichage de la quantité
    val currentQty = quantity ?: 0
    val badgeText = when {
        currentQty <= 0 -> "Plus de stock"
        currentQty == 1 -> "1 portion"
        else -> "$currentQty portions"
    }

    val image = imageUrl?.takeIf { it.isNotBlank() }
    val icon = when (titleText.lowercase()) {
        "fish fillet" -> Icons.Default.FavoriteBorder
        "chicken crisp" -> Icons.Default.NotificationsNone
        else -> Icons.Default.ShoppingCart
    }
    return HomeOfferCardUi(titleText, descriptionText, badgeText, image, icon)
}
