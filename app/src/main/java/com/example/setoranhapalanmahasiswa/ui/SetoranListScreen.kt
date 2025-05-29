package com.example.setoranhapalanmahasiswa.ui

import android.content.Intent
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.network.downloadKartuMurojaah
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import kotlinx.coroutines.launch
import java.io.File

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
    val scope = rememberCoroutineScope()

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
                    scope.launch {
                        val file = File(context.cacheDir, "kartu_murojaah.pdf")
                        val token = vm.token
                        val berhasil = downloadKartuMurojaah(token, file)

                        if (berhasil) {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Gagal mendownload kartu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
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
