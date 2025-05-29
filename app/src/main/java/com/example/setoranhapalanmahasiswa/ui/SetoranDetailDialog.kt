package com.example.setoranhapalanmahasiswa.ui

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
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus

@Composable
fun SetoranDetailDialog(
    setoranId: String,
    onDismiss: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { if (daftar.isEmpty()) vm.fetchSetoranList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onDismiss) { Text("Tutup") } },
        title = { Text("Detail  Setoran") },
        text = {
            when {
                status == LoadingStatus.LOADING ->
                    Box(Modifier.fillMaxWidth(), Alignment.Center) { CircularProgressIndicator() }

                error.isNotBlank() ->
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)

                else -> {
                    val s = daftar.find { it.id == setoranId }
                    if (s == null) {
                        Text("Data setoran tidak ditemukan.")
                    } else {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 8.dp)
                        ) {
                            InfoRow("Surah",              "${s.nama} (${s.nama_arab})")
                            InfoRow("Label",              s.label.ifBlank { "-" })
                            InfoRow("Sudah Setor",        if (s.sudah_setor) "Ya" else "Belum")
                            InfoRow("Tanggal Setor",      s.info_setoran?.tgl_setoran ?: "-")
                            InfoRow("Tanggal Validasi",   s.info_setoran?.tgl_validasi ?: "-")
                            InfoRow("Dosen Pengesah",     s.info_setoran?.dosen_yang_mengesahkan?.nama ?: "-")
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(12.dp))
        Text(value)
    }
}
