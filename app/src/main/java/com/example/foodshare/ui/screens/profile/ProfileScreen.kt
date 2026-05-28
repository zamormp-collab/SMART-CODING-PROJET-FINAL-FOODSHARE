package com.example.foodshare.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshare.ui.theme.BrownPrimary
import com.example.foodshare.ui.theme.DarkBackground
import com.example.foodshare.ui.theme.DarkSurface
import com.example.foodshare.ui.theme.OrangeAccent
import com.example.foodshare.ui.theme.WhiteText

@Composable
fun ProfileScreen(
	onBackClick: () -> Unit = {}
) {
	var nom by remember { mutableStateOf("Utilisateur") }
	var email by remember { mutableStateOf("utilisateur@foodshare.com") }
	var telephone by remember { mutableStateOf("") }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.safeDrawingPadding()
			.background(Brush.verticalGradient(listOf(BrownPrimary, DarkBackground)))
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Row(verticalAlignment = Alignment.CenterVertically) {
			Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)) {
				Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = WhiteText)
			}
			Spacer(modifier = Modifier.width(6.dp))
			Text(text = "Mon profil", color = WhiteText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
		}

		Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
				Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
					Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = OrangeAccent)) {
						Box(modifier = Modifier.size(68.dp), contentAlignment = Alignment.Center) {
							Icon(Icons.Default.Person, contentDescription = null, tint = WhiteText)
						}
					}
					Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
						Text(text = nom, color = WhiteText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
						Text(text = email, color = WhiteText)
						Text(text = "Cliquez sur l'avatar pour changer votre photo", color = WhiteText, fontSize = 12.sp)
					}
				}

				Card(colors = CardDefaults.cardColors(containerColor = BrownPrimary), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
					Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
						Text(text = "Informations du compte", color = WhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
						OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom complet") }, modifier = Modifier.fillMaxWidth())
						OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Adresse email") }, leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, modifier = Modifier.fillMaxWidth())
						OutlinedTextField(value = telephone, onValueChange = { telephone = it }, label = { Text("Téléphone") }, leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }, modifier = Modifier.fillMaxWidth())
					}
				}

				Button(onClick = { /* save later */ }, colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent), modifier = Modifier.fillMaxWidth()) {
					Text(text = "ENREGISTRER LES MODIFICATIONS")
				}
			}
		}
	}
}