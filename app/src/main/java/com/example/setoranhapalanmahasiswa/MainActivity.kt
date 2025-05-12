package com.example.setoranhapalanmahasiswa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.setoranhapalanmahasiswa.theme.SetoranHapalanmahasiswaTheme
import com.example.setoranhapalanmahasiswa.ui.DashboardScreen
import com.example.setoranhapalanmahasiswa.ui.LoginScreen
import com.example.setoranhapalanmahasiswa.ui.SetoranFormScreen
import com.example.setoranhapalanmahasiswa.ui.SetoranListScreen
import com.example.setoranhapalanmahasiswa.ui.VerifikasiScreen

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
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("setoran_list") { SetoranListScreen(navController) }
        composable("setoran_form") { SetoranFormScreen(navController) }
        composable("setoran_verifikasi/{id}") {
            val id = it.arguments?.getString("id")?.toIntOrNull() ?: 0
            VerifikasiScreen(id)
        }
    }
}
