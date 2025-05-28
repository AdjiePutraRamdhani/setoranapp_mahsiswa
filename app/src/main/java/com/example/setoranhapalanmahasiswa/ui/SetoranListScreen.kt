package com.example.setoranhapalanmahasiswa.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import com.example.setoranhapalanmahasiswa.util.exportSetoranToPdf

@Composable
fun SetoranListScreen(
    nav: NavHostController,
    vm: AuthViewModel = hiltViewModel()
) {
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()
    val userInfo by vm.userInfo.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Detail Riwayat Muroja'ah",
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = {
                    userInfo?.let {
                        exportSetoranToPdf(context, daftar, it)
                    } ?: Toast.makeText(context,
                        "Data pengguna belum tersedia",
                        Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download Rekap",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            status == LoadingStatus.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage.isNotBlank() -> {
                Text(
                    text = "Kesalahan: $errorMessage",
                    color = MaterialTheme.colorScheme.error
                )
            }

            daftar.isEmpty() -> {
                Text("Belum ada data setoran.")
            }

            else -> {
                LazyColumn {
                    items(daftar) { setoran: Setoran ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Surah: ${setoran.nama} (${setoran.nama_arab})")
                                Text("Status: ${if (setoran.sudah_setor) "Sudah" else "Belum"}")
                                Text("Label: ${setoran.label}")
                                Spacer(modifier = Modifier.height(8.dp))
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
}
