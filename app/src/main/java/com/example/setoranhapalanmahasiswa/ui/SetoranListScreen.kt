package com.example.setoranhapalanmahasiswa.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SetoranListScreen(nav: NavHostController, vm: AuthViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    var daftar by remember { mutableStateOf(listOf<Setoran>()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Ambil data saat pertama kali
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                daftar = vm.getSetoranList()
                Log.d("SetoranListScreen", "Berhasil ambil data: ${daftar.size} item")
                loading = false
            } catch (e: Exception) {
                errorMessage = "Gagal ambil data: ${e.message}"
                Log.e("SetoranListScreen", "Error saat ambil data", e)
                loading = false
            }
        }
    }

    // UI Utama
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daftar Setoran", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text("Terjadi kesalahan: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
            daftar.isEmpty() -> {
                Text("Belum ada data setoran.")
            }
            else -> {
                LazyColumn {
                    items(daftar) { setoran ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("${setoran.surah} : ${setoran.ayat} (${setoran.tanggal})")
                                Text("Status: ${setoran.status}")
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    nav.navigate("setoran_verifikasi/${setoran.id}")
                                }) {
                                    Text("Lihat Verifikasi")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
