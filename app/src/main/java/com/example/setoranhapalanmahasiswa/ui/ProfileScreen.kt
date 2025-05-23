package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus

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


    LaunchedEffect(vm.token) {
        if (vm.token.isNotEmpty()) {
            vm.fetchUserInfo(vm.token)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontSize = 20.sp) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (status) {
                LoadingStatus.LOADING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Mengambil data profil...", fontSize = 16.sp)
                    }
                }

                LoadingStatus.SUCCESS -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profil Pengguna",
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Profil Pengguna",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                ProfileItem(label = "Nama", value = nama)
                                ProfileItem(label = "Email", value = email)
                                ProfileItem(label = "NIM", value = nim)

                            }
                        }

                        Button(
                            onClick = {
                                vm.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Logout", fontSize = 16.sp)
                        }
                    }
                }

                LoadingStatus.ERROR -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    // Untuk menangani kasus default jika ada perubahan status di masa depan
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
