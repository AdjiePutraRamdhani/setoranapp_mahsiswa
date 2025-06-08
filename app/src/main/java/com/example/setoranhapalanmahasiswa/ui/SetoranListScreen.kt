package com.example.setoranhapalanmahasiswa.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.R
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.network.downloadKartuMurojaah
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetoranListScreen(
    nav: NavHostController,
    vm: AuthViewModel = hiltViewModel()
) {
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filteredList = daftar.filter { setoran ->
        val cocokNama = setoran.nama.contains(searchQuery, true) ||
                setoran.nama_arab.contains(searchQuery, true)

        val cocokStatus = when (selectedFilter) {
            "Sudah" -> setoran.sudah_setor
            "Belum" -> !setoran.sudah_setor
            else -> true
        }

        cocokNama && cocokStatus
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.detailmurojaah),
                    contentDescription = "Riwayat Muroja'ah Icon",
                    modifier = Modifier
                        .size(35.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Detail Riwayat Muroja'ah",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

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
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unduh),
                    contentDescription = "Download Rekap",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ AWAL PERUBAHAN UTAMA: Membungkus Search Bar dan Dropdown dalam satu Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Pastikan mereka sejajar secara vertikal
        ) {
            // Box (Search Bar) dengan modifier weight(1f)
            Box(
                modifier = Modifier
                    .weight(1f) // ⭐ Berikan weight agar mengambil sisa ruang
                    .height(56.dp) // Tinggi sama dengan TextField
                    .background(
                        brush = Brush.horizontalGradient( // Menggunakan gradien horizontal
                            colors = listOf(
                                Color.Red,
                                Color.Yellow,
                                Color.Green,
                                Color.Blue,
                                Color.Magenta
                            )
                        ),
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(2.dp)
                    .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Cari surah...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp)) // ⭐ Spasi antara Search Bar dan Dropdown

            // DropdownMenuBox
            DropdownMenuBox(
                selected = selectedFilter,
                onSelectedChange = { selectedFilter = it },
                options = listOf("Semua", "Sudah", "Belum")
            )
        }
        // ⭐ AKHIR PERUBAHAN UTAMA

        Spacer(modifier = Modifier.height(12.dp))

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

            filteredList.isEmpty() -> {
                Text("Tidak ditemukan data yang cocok.")
            }

            else -> {
                LazyColumn {
                    items(filteredList, key = { it.id }) { setoran ->
                        AnimatedVisibility(visible = true) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .shadow(6.dp, shape = MaterialTheme.shapes.medium),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFBBDEFB)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text("Surah: ${setoran.nama} (${setoran.nama_arab})")

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Status: ",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 4.dp)
                                                    .background(
                                                        color = if (setoran.sudah_setor)
                                                            Color(0xFFB9F6CA)
                                                        else
                                                            Color(0xFFFFCDD2),
                                                        shape = MaterialTheme.shapes.small
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (setoran.sudah_setor) "Sudah" else "Belum",
                                                    color = if (setoran.sudah_setor)
                                                        Color(0xFF2E7D32)
                                                    else
                                                        Color(0xFFC62828),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Label: ${setoran.label}")
                                    }

                                    IconButton(
                                        onClick = {
                                            nav.navigate("setoran_verifikasi/${setoran.id}")
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Detail",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    selected: String,
    onSelectedChange: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = true,
            onClick = { expanded = true },
            label = { Text(selected) },
            // ⭐ Penting: Sesuaikan tinggi FilterChip agar sejajar dengan TextField
            modifier = Modifier.height(56.dp) // Pastikan tingginya sama dengan TextField
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}