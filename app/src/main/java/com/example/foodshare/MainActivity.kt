package com.example.foodshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.foodshare.data.local.SessionManager
import com.example.foodshare.data.remote.RetrofitClient
import com.example.foodshare.ui.navigation.NavGraph
import com.example.foodshare.ui.theme.FoodShareTheme
import com.example.foodshare.viewmodel.AuthViewModel
import com.example.foodshare.viewmodel.AuthViewModelFactory
import com.example.foodshare.viewmodel.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodShareTheme {
                FoodShareApp()
            }
        }
    }
}

@Composable
fun FoodShareApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val factory = AuthViewModelFactory(RetrofitClient.authApiService, sessionManager)

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val registerViewModel: RegisterViewModel = viewModel(factory = factory)

    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        NavGraph(
            navController = navController,
            authViewModel = authViewModel,
            registerViewModel = registerViewModel
        )
    }
}