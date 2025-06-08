package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.Image // Import untuk Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color // Import untuk Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Import untuk painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.setoranhapalanmahasiswa.R // Pastikan ini diimpor untuk R.drawable.latar2
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

    // ⭐ Mulai dari sini: Tambahkan Box untuk menampung background dan dialog
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Gambar Background Utama untuk dialog
        Image(
            painter = painterResource(id = R.drawable.latar), // Ganti dengan gambar background Anda
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1.0f), // Atur alpha agar tidak transparan
            contentScale = ContentScale.Crop
        )

        // Overlay untuk background (optional, untuk membuat dialog lebih menonjol)
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Overlay hitam transparan
        )

        // Pastikan AlertDialog berada di tengah Box
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onDismiss) { Text("Tutup") } },
            title = { Text("Detail Setoran", color = Color.White) }, // ⭐ Ubah warna teks title
            text = {
                when {
                    status == LoadingStatus.LOADING ->
                        Box(Modifier.fillMaxWidth(), Alignment.Center) { CircularProgressIndicator() }

                    error.isNotBlank() ->
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)

                    else -> {
                        val s = daftar.find { it.id == setoranId }
                        if (s == null) {
                            Text("Data setoran tidak ditemukan.", color = Color.White) // ⭐ Ubah warna teks
                        } else {
                            Column(
                                Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(bottom = 8.dp)
                            ) {
                                InfoRow("Surah", s.nama + " (" + s.nama_arab + ")")
                                InfoRow("Label", s.label.ifBlank { "-" })
                                InfoRow("Sudah Setor", if (s.sudah_setor) "Ya" else "Belum")
                                InfoRow("Tanggal Setor", s.info_setoran?.tgl_setoran ?: "-")
                                InfoRow("Tanggal Validasi", s.info_setoran?.tgl_validasi ?: "-")
                                InfoRow("Dosen Pengesah", s.info_setoran?.dosen_yang_mengesahkan?.nama ?: "-")
                            }
                        }
                    }
                }
            },
            // ⭐ Di sini kita mengatur warna background AlertDialog menjadi biru
            containerColor = Color(0xFF4CAF50), // Contoh warna biru yang lebih gelap, Anda bisa sesuaikan
            // Atau MaterialTheme.colorScheme.primary kalau warna primary Anda biru
            // Atau Color(0xFF2196F3) untuk biru standar
            // Atau Color.Blue jika ingin biru murni
            // ⭐ Sesuaikan warna teks di dalam dialog agar tetap terbaca di atas warna biru
            textContentColor = Color.White,
            titleContentColor = Color.White
        )
    } // Akhir dari Box pembungkus
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color.White) // ⭐ Ubah warna teks
        Spacer(Modifier.width(12.dp))
        Text(value, color = Color.White) // ⭐ Ubah warna teks
    }
}