package com.example.foodshare.ui.screens.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.GrayText
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText
import com.google.gson.Gson

@Composable
fun ProfileScreen(
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }

    var user by remember { mutableStateOf(loadUser(sessionManager)) }
    var avatarUri by remember { mutableStateOf(parseAvatarUri(user)) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        avatarUri = uri
    }

    LaunchedEffect(Unit) {
        if (user == null) {
            user = loadUser(sessionManager)
            avatarUri = parseAvatarUri(user)
        }
    }

    val displayUser = user
    val displayName = displayUser?.nom?.takeIf { it.isNotBlank() } ?: "Utilisateur"
    val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrownPrimary, DarkBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = WhiteText,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackClick?.invoke() }
                )

                Text(
                    text = "Mon profil",
                    color = WhiteText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(132.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3A2A22)),
                        contentAlignment = Alignment.Center
                    ) {
                        val bitmap = avatarUri?.let { rememberBitmapFromUri(context, it) }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Photo de profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = initial,
                                color = OrangeAccent,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OrangeAccent)
                                .clickable { pickImageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Modifier la photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = displayName,
                        color = WhiteText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = displayUser?.role?.takeIf { it.isNotBlank() } ?: "-",
                        color = GrayText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Changer la photo", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Informations du compte",
                color = WhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(label = "Nom", value = displayUser?.nom ?: "-")
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInfoCard(label = "Prénom", value = displayUser?.prenom ?: "-")
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInfoCard(label = "Email", value = displayUser?.email ?: "-")
            Spacer(modifier = Modifier.height(10.dp))
            ProfileInfoCard(label = "Rôle", value = displayUser?.role ?: "-")

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val updated = displayUser?.copy(avatarUrl = avatarUri?.toString())
                    if (updated != null) {
                        sessionManager.saveUserJson(Gson().toJson(updated))
                        user = updated
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
            ) {
                Text(
                    text = "Enregistrer la photo",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Les autres informations sont récupérées automatiquement depuis l'inscription.",
                color = GrayText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileInfoCard(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                color = GrayText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = WhiteText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun loadUser(sessionManager: SessionManager): UserDto? {
    val json = sessionManager.getUserJson() ?: return null
    return try {
        Gson().fromJson(json, UserDto::class.java)
    } catch (_: Exception) {
        null
    }
}

private fun parseAvatarUri(user: UserDto?): Uri? {
    val raw = user?.avatarUrl?.takeIf { it.isNotBlank() } ?: return null
    return runCatching { Uri.parse(raw) }.getOrNull()
}

private fun rememberBitmapFromUri(context: Context, uri: Uri): ImageBitmap? {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)?.asImageBitmap()
        }
    }.getOrNull()
}