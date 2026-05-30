package com.example.foodshare.ui.screens.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.dto.UserDto
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.FoodShareTheme
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

    var user by remember { mutableStateOf<UserDto?>(null) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            avatarUri = uri
        }
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            val loadedUser = loadUser(sessionManager)
            user = loadedUser
            avatarUri = parseAvatarUri(loadedUser)
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Erreur lors du chargement du profil"
            isLoading = false
        }
    }

    ProfileScreenContent(
        user = user,
        avatarUri = avatarUri,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onBackClick = onBackClick,
        onPhotoChange = { avatarUri = it },
        onSave = { updatedUser ->
            if (updatedUser != null) {
                sessionManager.saveUserJson(Gson().toJson(updatedUser))
                user = updatedUser
            }
        },
        onPickImage = { pickImageLauncher.launch("image/*") },
        context = context
    )
}

@Composable
private fun ProfileScreenContent(
    user: UserDto?,
    avatarUri: Uri?,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: (() -> Unit)?,
    onPhotoChange: (Uri) -> Unit,
    onSave: (UserDto?) -> Unit,
    onPickImage: () -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4EE))
    ) {
        ProfileHeaderSection(
            user = user,
            avatarUri = avatarUri,
            onBackClick = onBackClick,
            onPickImage = onPickImage,
            context = context
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
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
                ) {
                    ProfileLoadingState()
                }

                AnimatedVisibility(
                    visible = !errorMessage.isNullOrBlank(),
                    enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
                ) {
                    ProfileErrorState(message = errorMessage.orEmpty())
                }

                AnimatedVisibility(
                    visible = user != null && !isLoading && errorMessage.isNullOrBlank(),
                    enter = fadeIn(animationSpec = tween(220)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(180)) + shrinkVertically()
                ) {
                    user?.let { currentUser ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProfileIdentityCard(user = currentUser)

                            SectionTitleText(title = "Informations personnelles")

                            ProfileInfoCard(
                                icon = Icons.Default.Person,
                                label = "Nom",
                                value = currentUser.nom?.takeIf { it.isNotBlank() } ?: "-"
                            )

                            ProfileInfoCard(
                                icon = Icons.Default.PersonOutline,
                                label = "Prénom",
                                value = currentUser.prenom?.takeIf { it.isNotBlank() } ?: "-"
                            )

                            ProfileInfoCard(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = currentUser.email?.takeIf { it.isNotBlank() } ?: "-"
                            )

                            ProfileInfoCard(
                                icon = Icons.Default.Lock,
                                label = "Rôle",
                                value = currentUser.role?.takeIf { it.isNotBlank() } ?: "-"
                            )

                            SectionTitleText(title = "Compte")

                            ProfileMenuItemCard(
                                icon = Icons.Default.PersonOutline,
                                label = "Informations du compte",
                                value = "Gérer les paramètres"
                            )

                            ProfileMenuItemCard(
                                icon = Icons.Default.CameraAlt,
                                label = "Photo de profil",
                                value = "Changer votre avatar"
                            )

                            ProfileMenuItemCard(
                                icon = Icons.Default.Security,
                                label = "Sécurité",
                                value = "Gérer la sécurité du compte"
                            )

                            ProfileMenuItemCard(
                                icon = Icons.Default.Lock,
                                label = "Conditions d'utilisation",
                                value = "Lire les conditions"
                            )

                            ProfileMenuItemCard(
                                icon = Icons.Default.Logout,
                                label = "Déconnexion",
                                value = "Quitter l'application"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val updated = currentUser.copy(avatarUrl = avatarUri?.toString())
                                    onSave(updated)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enregistrer les modifications",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                if (user == null && !isLoading && errorMessage.isNullOrBlank()) {
                    ProfileEmptyState()
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderSection(
    user: UserDto?,
    avatarUri: Uri?,
    onBackClick: (() -> Unit)?,
    onPickImage: () -> Unit,
    context: Context
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        color = Color(0xFFF8F4EE)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color(0xFF1F1F1F),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackClick?.invoke() }
                )

                Text(
                    text = "Mon profil",
                    color = Color(0xFF1F1F1F),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(28.dp))
            }

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(BrownPrimary)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = avatarUri?.let { rememberBitmapFromUri(context, it) }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Photo de profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = user?.nom?.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        color = OrangeAccent,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(OrangeAccent)
                        .clickable { onPickImage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Changer la photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = user?.nom?.takeIf { it.isNotBlank() } ?: "Utilisateur",
                    color = Color(0xFF1F1F1F),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = user?.email?.takeIf { it.isNotBlank() } ?: "-",
                    color = GrayText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProfileIdentityCard(user: UserDto) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = user.nom?.takeIf { it.isNotBlank() } ?: "Utilisateur",
                color = Color(0xFF1F1F1F),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = user.email?.takeIf { it.isNotBlank() } ?: "-",
                color = GrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = user.role?.takeIf { it.isNotBlank() } ?: "Utilisateur",
                color = OrangeAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = OrangeAccent,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    color = GrayText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    color = WhiteText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItemCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = OrangeAccent,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    color = WhiteText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    color = GrayText,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = GrayText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SectionTitleText(title: String) {
    Text(
        text = title,
        color = WhiteText,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun ProfileLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = OrangeAccent,
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileErrorState(message: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A2320)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Erreur",
                tint = Color(0xFFE6755A),
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = message,
                color = Color(0xFFE6755A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProfileEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Vide",
            tint = GrayText,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = "Aucune information disponible",
            color = GrayText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    FoodShareTheme {
        ProfileScreenContent(
            user = UserDto(
                id = "1",
                nom = "Patrick",
                prenom = "Delva",
                email = "patrick@example.com",
                role = "Utilisateur",
                avatarUrl = null
            ),
            avatarUri = null,
            isLoading = false,
            errorMessage = null,
            onBackClick = { },
            onPhotoChange = { },
            onSave = { },
            onPickImage = { },
            context = LocalContext.current
        )
    }
}
