package com.example.setoranhapalanmahasiswa.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun SetoranFormScreen(nav: NavHostController, vm: AuthViewModel = viewModel()) {
    var surah by remember { mutableStateOf("") }
    var ayat by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }

    var surahError by remember { mutableStateOf(false) }
    var ayatError by remember { mutableStateOf(false) }
    var tanggalError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year, month, dayOfMonth ->
                tanggal = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Form Setoran Hafalan",
            style = MaterialTheme.typography.titleLarge
        )

        // Nama Surah
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nama Surah",
                    modifier = Modifier.width(100.dp)
                )
                OutlinedTextField(
                    value = surah,
                    onValueChange = {
                        surah = it
                        surahError = false
                    },
                    isError = surahError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (surahError) {
                Text(
                    text = "Surah harus diisi",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 100.dp) // Sejajar dengan input
                )
            }
        }

        // Ayat
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ayat",
                    modifier = Modifier.width(100.dp)
                )
                OutlinedTextField(
                    value = ayat,
                    onValueChange = {
                        ayat = it
                        ayatError = false
                    },
                    isError = ayatError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (ayatError) {
                Text(
                    text = "Ayat harus diisi",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 100.dp) // Sejajar dengan input
                )
            }
        }

        // Tanggal
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tanggal",
                    modifier = Modifier.width(100.dp)
                )
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = {
                        tanggal = it
                        tanggalError = false
                    },
                    label = { Text("dd/mm/yyyy") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih tanggal")
                        }
                    },
                    isError = tanggalError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (tanggalError) {
                Text(
                    text = "Tanggal harus diisi",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 100.dp) // Sejajar dengan input
                )
            }
        }

        // Tombol Kirim dan Batal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    nav.navigate("dashboard") {
                        popUpTo("setoran_form") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("Batal")
            }

            Button(
                onClick = {
                    var valid = true
                    if (surah.isBlank()) {
                        surahError = true
                        valid = false
                    }
                    if (ayat.isBlank()) {
                        ayatError = true
                        valid = false
                    }
                    if (tanggal.isBlank()) {
                        tanggalError = true
                        valid = false
                    }

                    if (valid) {
                        scope.launch {
                            vm.submitSetoran(surah, ayat, tanggal)
                            nav.navigate("dashboard") {
                                popUpTo("setoran_form") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("Kirim")
            }
        }
    }
}
