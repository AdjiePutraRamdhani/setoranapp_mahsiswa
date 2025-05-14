package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
    var currentScreen by remember { mutableStateOf("home") }
    val tabs = listOf("Daftar Setoran", "Statistik Setoran")
    var selectedTabIndex by remember { mutableStateOf(0) }

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

                        Text(
                            text = when (currentScreen) {
                                "home" -> "Dashboard"
                                "profile" -> "Profil Saya"
                                "statistik" -> "Statistik Setoran"
                                else -> ""
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        if (currentScreen == "home") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (vm.nama.isNotEmpty()) "Selamat datang, ${vm.nama}" else "Memuat data...",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 10.dp)
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
                    // Tab untuk Beranda (Setoran & Statistik)
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedTabIndex) {
                        0 -> SetoranListScreen(nav, vm)
                        1 -> SetoranFormScreen(nav, vm)
                    }
                }

                "statistik" -> {
                    Text(
                        "Statistik Setoran",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 32.dp)
                    )
                }

                "profile" -> {
                    ProfileScreen(nav)
                }
            }
        }
    }

    println("NAMA YANG DITAMPILKAN: ${vm.nama}")
}

