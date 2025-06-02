package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.setoranhapalanmahasiswa.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
    var currentScreen by remember { mutableStateOf("home") }
    val nama by vm.nama.collectAsState()
    val email by vm.email.collectAsState()
    val nim by vm.nim.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (currentScreen == "profile") {
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (currentScreen == "home") {
                            // Tampilkan logo dan teks "Dashboard"
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logouinsuskariau), // ganti sesuai nama file di drawable
                                    contentDescription = "Logo UIN",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = "Dashboard",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        } else {
                            Text(
                                text = when (currentScreen) {
                                    "profile" -> "Profil Saya"
                                    "statistik" -> "Statistik Setoran"
                                    else -> ""
                                },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda", fontSize = 12.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "statistik",
                    onClick = { currentScreen = "statistik" },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Statistik") },
                    label = { Text("Statistik", fontSize = 12.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { currentScreen = "profile" },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 12.sp) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (currentScreen) {
                "home" -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    SetoranListScreen(nav, vm)
                }

                "statistik" -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    SetoranFormScreen(nav, vm)
                }

                "profile" -> {
                    ProfileScreen(nav)
                }
            }
        }
    }
    println("NAMA YANG DITAMPILKAN: $nama")
}