package com.example.setoranhapalanmahasiswa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.setoranhapalanmahasiswa.theme.SetoranHapalanmahasiswaTheme
import com.example.setoranhapalanmahasiswa.ui.*
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetoranHafalanApp()
        }
    }
}

@Composable
fun SetoranHafalanApp() {
    val navController = rememberNavController()
    val vm: AuthViewModel = hiltViewModel()

    SetoranHapalanmahasiswaTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { LoginScreen(navController) }
            composable("dashboard") { DashboardScreen(navController) }
            composable("profile") { ProfileScreen(navController = navController) }
            composable("setoran_list") { SetoranListScreen(navController) }
            composable("setoran_form") { SetoranFormScreen(navController) }
        }
    }
}
