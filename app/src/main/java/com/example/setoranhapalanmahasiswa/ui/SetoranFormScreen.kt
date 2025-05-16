package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus

@Composable
fun SetoranFormScreen(
    nav: NavHostController,
    vm: AuthViewModel = hiltViewModel()
) {
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Progress Muroja'ah",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                status == LoadingStatus.LOADING -> {
                    CircularProgressIndicator(
                        color = colors.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                errorMessage.isNotBlank() -> {
                    Text(
                        text = "Error: $errorMessage",
                        color = colors.error,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }

                daftar.isEmpty() -> {
                    Text(
                        text = "Belum ada data setoran.",
                        fontSize = 18.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                else -> {
                    val totalSetoran = daftar.size
                    val sudahSetor = daftar.count { it.sudah_setor }
                    val belumSetor = totalSetoran - sudahSetor
                    val persentaseDisplay = if (totalSetoran > 0) {
                        (sudahSetor * 100) / totalSetoran
                    } else 0

                    val warnaSudahSetor = Color(0xFF6699FF) // Biru
                    val warnaBelumSetor = Color(0xFFFF385B) // Merah

                    val values = listOf(
                        sudahSetor.toFloat(),
                        belumSetor.toFloat()
                    )
                    val totalValues = values.sum()

                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(220.dp)) {
                            var startAngle = -90f
                            val gapAngle = 2f

                            values.forEachIndexed { index, value ->
                                val sweepAngle = (value / totalValues) * 360f

                                // Gap putih
                                drawArc(
                                    color = Color.White,
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = true,
                                    size = size
                                )

                                // Slice warna
                                drawArc(
                                    color = if (index == 0) warnaSudahSetor else warnaBelumSetor,
                                    startAngle = startAngle + gapAngle / 2,
                                    sweepAngle = sweepAngle - gapAngle,
                                    useCenter = true,
                                    size = size
                                )

                                startAngle += sweepAngle
                            }
                        }

                        Text(
                            text = "$persentaseDisplay%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legenda warna
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ColorLegendBox(color = warnaSudahSetor, label = "Sudah Setor")
                        ColorLegendBox(color = warnaBelumSetor, label = "Belum Setor")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                colors.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow("Total Setoran", "$totalSetoran")
                        InfoRow("Sudah Setor", "$sudahSetor")
                        InfoRow("Belum Setor", "$belumSetor")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorLegendBox(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary
        )
    }
}
