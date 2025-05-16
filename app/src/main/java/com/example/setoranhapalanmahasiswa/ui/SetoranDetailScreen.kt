package com.example.setoranhapalanmahasiswa.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import kotlinx.coroutines.launch

@Composable
fun SetoranDetailScreen(
    setoranId: String,
    nav: NavHostController,
    vm: AuthViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()

    var setoran by remember { mutableStateOf<Setoran?>(null) }

    LaunchedEffect(setoranId) {
        scope.launch {
            try {
                setoran = daftar.find { it.id == setoranId }
                if (setoran == null) {
                    vm.fetchSetoranList()
                    setoran = vm.setoranList.value.find { it.id == setoranId }
                }
            } catch (e: Exception) {
                Log.e("SetoranDetailScreen", "Error: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detail Verifikasi Setoran", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        when {
            status == LoadingStatus.LOADING -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            errorMessage.isNotBlank() -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            setoran != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tanggal Setoran: ${setoran?.info_setoran?.tgl_setoran ?: "Tidak tersedia"}", fontWeight = FontWeight.Medium)
                        Text("Tanggal Validasi: ${setoran?.info_setoran?.tgl_validasi ?: "Belum divalidasi"}", fontWeight = FontWeight.Medium)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Dosen Pengesah: ${setoran?.info_setoran?.dosen_yang_mengesahkan?.nama ?: "Tidak diketahui"}", fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { nav.popBackStack() }) {
                    Text("Kembali")
                }
            }
            else -> {
                Text("Data setoran tidak ditemukan.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
