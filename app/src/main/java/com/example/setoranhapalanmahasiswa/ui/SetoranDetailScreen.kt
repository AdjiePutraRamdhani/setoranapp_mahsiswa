package com.example.setoranhapalanmahasiswa.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import kotlinx.coroutines.launch

@Composable
fun SetoranDetailScreen(setoranId: String, nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()

    var setoran by remember { mutableStateOf<Setoran?>(null) }

    LaunchedEffect(setoranId) {
        scope.launch {
            try {
                // Cek apakah data sudah ada di daftar setoran
                setoran = daftar.find { it.id == setoranId }

                // Jika data tidak ditemukan, ambil ulang dari API
                if (setoran == null) {
                    vm.fetchSetoranList()
                    setoran = vm.setoranList.value.find { it.id == setoranId }
                }

                if (setoran != null) {
                    Log.d("SetoranDetailScreen", "Data setoran ditemukan: ${setoran?.nama} (${setoran?.nama_arab})")
                } else {
                    Log.d("SetoranDetailScreen", "Data setoran tidak ditemukan untuk ID: $setoranId")
                }
            } catch (e: Exception) {
                Log.e("SetoranDetailScreen", "Error saat mengambil data setoran: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Detail Verifikasi Setoran", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        when {
            status == LoadingStatus.LOADING -> CircularProgressIndicator()
            errorMessage.isNotBlank() -> Text(
                "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error
            )
            setoran != null -> {
                // Penanganan info setoran
                setoran?.info_setoran?.let { info ->
                    Text("Tanggal Setoran: ${info.tgl_setoran ?: "Tidak tersedia"}")
                    Text("Tanggal Validasi: ${info.tgl_validasi ?: "Belum divalidasi"}")

                    // Penanganan dosen
                    info.dosen_yang_mengesahkan?.let { dosen ->
                        Text("Dosen Pengesah: ${dosen.nama ?: "Tidak diketahui"}")
                        Log.d("SetoranDetailScreen", "Dosen: ${dosen.nama}")
                    } ?: Text("Dosen Pengesah: Tidak ada data")
                } ?: Text("Belum Disetor")

                Spacer(Modifier.height(8.dp))
                Button(onClick = { nav.popBackStack() }) {
                    Text("Kembali")
                }
            }
            else -> Text("Data setoran tidak ditemukan.")
        }
    }
}


