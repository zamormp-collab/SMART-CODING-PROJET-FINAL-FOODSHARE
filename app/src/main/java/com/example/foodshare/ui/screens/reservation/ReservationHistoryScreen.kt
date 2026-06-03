package com.example.foodshare.ui.screens.reservation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.ReservationDto
import com.example.foodshare.ui.components.ErrorMessage
import com.example.foodshare.ui.components.LoadingIndicator
import com.example.foodshare.ui.theme.GrayText
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.viewmodel.ReservationState
import com.example.foodshare.viewmodel.ReservationViewModel
import com.example.foodshare.viewmodel.ReservationViewModelFactory

@Composable
fun ReservationHistoryScreen(
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val reservationViewModel: ReservationViewModel = viewModel(
        factory = remember(sessionManager) { ReservationViewModelFactory(sessionManager) }
    )
    val uiState = reservationViewModel.uiState

    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedReservationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (uiState is ReservationState.Idle) {
            reservationViewModel.loadReservations()
        }
    }

    val isLoading = uiState is ReservationState.Loading
    val errorMessage = (uiState as? ReservationState.Error)?.message
    val reservations = (uiState as? ReservationState.Success)?.reservations ?: emptyList()

    if (showReviewDialog && selectedReservationId != null) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { note, comment ->
                reservationViewModel.submitReview(selectedReservationId!!, note, comment) {
                    showReviewDialog = false
                }
            },
            isSubmitting = reservationViewModel.isSubmittingReview
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Retour",
                modifier = Modifier
                    .padding(4.dp)
                    .clickableWithoutRipple { onBackClick?.invoke() },
                tint = Color.Black
            )

            Text(
                text = "Mes réservations",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> LoadingIndicator()
                !errorMessage.isNullOrBlank() -> ErrorMessage(message = errorMessage)
                reservations.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Aucune réservation trouvée")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reservations) { r ->
                            ReservationItem(
                                reservation = r,
                                onReviewClick = { id ->
                                    selectedReservationId = id
                                    showReviewDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationItem(
    reservation: ReservationDto,
    onReviewClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reservation.offreTitre ?: "Offre inconnue",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Réservé le: ${reservation.dateReservation ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayText
                    )
                }

                if (reservation.statut?.contains("Récupérée", ignoreCase = true) == true) {
                    TextButton(
                        onClick = { reservation.id?.let { onReviewClick(it) } },
                        colors = ButtonDefaults.textButtonColors(contentColor = OrangeAccent)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Noter", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reservation.statut ?: "En attente",
                style = MaterialTheme.typography.bodyMedium,
                color = OrangeAccent,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit,
    isSubmitting: Boolean
) {
    var note by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Votre avis") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Comment s'est passée la récupération ?")
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        Icon(
                            imageVector = if (starIndex <= note) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (starIndex <= note) OrangeAccent else GrayText,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { note = starIndex }
                        )
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Commentaire (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(note, comment) },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Envoyer")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Annuler")
            }
        }
    )
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}
