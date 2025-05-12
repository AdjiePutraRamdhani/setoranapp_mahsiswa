package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel

@Composable
fun SetoranFormScreen(nav: NavHostController, vm: AuthViewModel = viewModel()) {
    val setoranList = vm.setoranList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Progress Muroja'ah",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        if (setoranList.isEmpty()) {
            Text("Belum ada data setoran.", fontSize = 18.sp)
        } else {
            val totalSetoran = setoranList.size
            val sudahSetor = setoranList.count { it.sudah_setor }
            val belumSetor = totalSetoran - sudahSetor

            Text("Total Setoran: $totalSetoran", fontSize = 20.sp)
            Text("Sudah Setor: $sudahSetor", fontSize = 20.sp)
            Text("Belum Setor: $belumSetor", fontSize = 20.sp)
        }
    }
}
