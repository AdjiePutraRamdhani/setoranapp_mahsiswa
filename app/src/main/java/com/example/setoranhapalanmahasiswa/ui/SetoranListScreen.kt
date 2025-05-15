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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import kotlinx.coroutines.launch

@Composable
fun SetoranListScreen(nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Detail Riwayat Muroja'ah", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            status == LoadingStatus.LOADING -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            errorMessage.isNotBlank() -> Text(
                text = "Kesalahan: $errorMessage",
                color = MaterialTheme.colorScheme.error
            )
            daftar.isEmpty() -> Text("Belum ada data setoran.")
            else -> LazyColumn {
                items(daftar) { setoran ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Surah: ${setoran.nama} (${setoran.nama_arab})")
                            Text("Status: ${if (setoran.sudah_setor) "Sudah" else "Belum"}")
                            Text("Label: ${setoran.label}")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                nav.navigate("setoran_verifikasi/${setoran.id}")
                            }) {
                                Text("Detail")
                            }
                        }
                    }
                }
            }
        }
    }
}



