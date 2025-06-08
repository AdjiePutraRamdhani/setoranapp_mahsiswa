package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.R
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
    var currentScreen by remember { mutableStateOf("home") }
    val nama by vm.nama.collectAsState()
    val email by vm.email.collectAsState()
    val nim by vm.nim.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.latar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1.0f),
            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f))
        )

        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .shadow(elevation = 6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.naavigasii),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(1.0f),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.0f))
                    )
                    TopAppBar(
                        title = {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 4.dp), // ⭐ MENAMBAHKAN PADDING ATAS DI SINI
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (currentScreen == "home") {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.logouinsuskariau),
                                            contentDescription = "Logo UIN",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(end = 8.dp)
                                                .shadow(
                                                    elevation = 8.dp,
                                                    shape = RoundedCornerShape(8.dp),
                                                    spotColor = Color.Black
                                                )
                                        )
                                        Text(
                                            text = "Dashboard",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.shadow(
                                                elevation = 8.dp,
                                                spotColor = Color.Black
                                            )
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
                                        color = Color.Black
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .shadow(elevation = 6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.naavigasii),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(1.0f),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.0f))
                    )
                    NavigationBar(
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == "home",
                                onClick = { currentScreen = "home" },
                                icon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.iconberanda),
                                        contentDescription = "Beranda",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text("Beranda", fontSize = 12.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = Color.White,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = Color.White,
                                    indicatorColor = Color.Yellow.copy(alpha = 0.3f)
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen == "statistik",
                                onClick = { currentScreen = "statistik" },
                                icon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.statistics),
                                        contentDescription = "Statistik",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text("Statistik", fontSize = 12.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = Color.White,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = Color.White,
                                    indicatorColor = Color.Yellow.copy(alpha = 0.3f)
                                )
                            )
                            NavigationBarItem(
                                selected = currentScreen == "profile",
                                onClick = { currentScreen = "profile" },
                                icon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.profile),
                                        contentDescription = "Profil",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text("Profil", fontSize = 12.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = Color.White,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = Color.White,
                                    indicatorColor = Color.Yellow.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) { padding ->
            AnimatedContent(
                targetState = currentScreen,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                label = "AnimatedContentScreen"
            ) { screen ->
                when (screen) {
                    "home" -> SetoranListScreen(nav, vm)
                    "statistik" -> SetoranFormScreen(nav, vm)
                    "profile" -> ProfileScreen(nav)
                }
            }
        }
    }

    println("NAMA YANG DITAMPILKAN: $nama")
}