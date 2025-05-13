package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: AuthViewModel = hiltViewModel()) {
    // Mengambil data profil saat pertama kali ditampilkan
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (vm.status) {
                LoadingStatus.LOADING -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mengambil data profil...", fontSize = 16.sp)
                }
                LoadingStatus.SUCCESS -> {
                    Text("Nama:", fontSize = 16.sp)
                    Text(vm.nama, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Email:", fontSize = 16.sp)
                    Text(vm.email, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Text("Username (NIM):", fontSize = 16.sp)
                    Text(vm.nim, fontSize = 18.sp)
                }
                LoadingStatus.ERROR -> {
                    Text("Gagal mengambil data profil", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                    Text(vm.error, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}
