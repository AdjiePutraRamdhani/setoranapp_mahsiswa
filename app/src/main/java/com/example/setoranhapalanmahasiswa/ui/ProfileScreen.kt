package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.setoranhapalanmahasiswa.R
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import androidx.compose.ui.graphics.Color
// Pastikan tidak ada import RoundedCornerShape yang konflik jika ada, karena sudah tidak digunakan secara langsung di ProfileScreen ini.
// MaterialTheme.shapes.medium sudah menggunakan RoundedCornerShape secara internal jika didefinisikan di tema.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    vm: AuthViewModel = hiltViewModel()
) {
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()
    val nama by vm.nama.collectAsState()
    val email by vm.email.collectAsState()
    val nim by vm.nim.collectAsState()
    val angkatan by vm.angkatan.collectAsState()
    val semester by vm.semester.collectAsState()
    val dosenPaNama by vm.dosenPaNama.collectAsState()
    val dosenPaEmail by vm.dosenPaEmail.collectAsState()
    val dosenPaNip by vm.dosenPaNip.collectAsState()

    LaunchedEffect(vm.token) {
        if (vm.token.isNotEmpty()) {
            vm.fetchUserInfo(vm.token)
        }
    }

    // Menggunakan Box terluar untuk background gambar dan overlay
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Gambar Background Utama (transparansi diatur di sini)
        Image(
            painter = painterResource(id = R.drawable.latar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1.0f), // <-- Atur transparansi gambar background di sini (0.0f - 1.0f)
            contentScale = ContentScale.Crop
        )

        // 2. Lapisan Overlay untuk Membuat Konten Lebih Terbaca
        // Sesuaikan warna dan alpha sesuai keinginan Anda, ini adalah lapisan penengah
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f)) // <-- Transparansi overlay (0.0f - 1.0f)
        )

        // Scaffold yang berisi TopAppBar dan Konten Utama
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    // Penting: Jadikan TopAppBar transparan agar gambar dan overlay terlihat
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            // Penting: Jadikan containerColor Scaffold transparan agar gambar dan overlay di bawahnya terlihat
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) { padding ->
            // Konten Utama Screen, akan di-padding oleh Scaffold
            when (status) {
                LoadingStatus.LOADING -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Mengambil data profil...", fontSize = 16.sp)
                    }
                }

                LoadingStatus.SUCCESS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                shape = MaterialTheme.shapes.medium,
                                // --- WARNA CARD DI SINI ---
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFADD8E6) // Contoh warna biru muda (Light Blue)
                                    // Anda bisa ganti dengan Color.Blue atau MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.iconman),
                                            contentDescription = "Foto Profil",
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Profil Pengguna",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = "Mahasiswa",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    ProfileItem("Nama", nama)
                                    ProfileItem("Email", email)
                                    ProfileItem("NIM", nim)
                                    ProfileItem("Angkatan", angkatan)
                                    ProfileItem("Semester", semester.toString())

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Dosen PA",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    ProfileItem("Nama", dosenPaNama)
                                    ProfileItem("Email", dosenPaEmail)
                                    ProfileItem("NIP", dosenPaNip)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    vm.logout()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text("Logout", fontSize = 16.sp)
                            }
                        }
                    }
                }

                LoadingStatus.ERROR -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Gagal mengambil data profil",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = errorMessage.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                else -> {
                    Text("Status tidak diketahui", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}