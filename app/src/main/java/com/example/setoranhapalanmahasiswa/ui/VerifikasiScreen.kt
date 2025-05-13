package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun VerifikasiScreen(id: Int, vm: AuthViewModel = hiltViewModel()) {
    var penilaian by remember { mutableStateOf("Memuat...") }

    LaunchedEffect(id) {
        penilaian = vm.getVerifikasi(id).toString()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Penilaian Setoran:", style = MaterialTheme.typography.headlineSmall)
        Text(penilaian)
    }
}


