package com.example.setoranhapalanmahasiswa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.setoranhapalanmahasiswa.network.ApiClient
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
        NavHost(navController, startDestination = "login") {
            composable("login") { LoginScreen(navController) }
            composable("dashboard") { DashboardScreen(navController) }
            composable("profile") { ProfileScreen(navController = navController) } // ✅ Perubahan di sini
            composable("setoran_list") { SetoranListScreen(navController) }
            composable("setoran_form") { SetoranFormScreen(navController) }
            composable("setoran_verifikasi/{id}") {
                val id = it.arguments?.getString("id")?.toIntOrNull() ?: 0
                VerifikasiScreen(id)
            }
        }
    }
}
