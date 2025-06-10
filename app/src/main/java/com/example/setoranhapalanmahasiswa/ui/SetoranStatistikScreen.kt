package com.example.setoranhapalanmahasiswa.ui

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.model.RingkasanSetoran
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.shadow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SetoranFormScreen(
    nav: NavHostController,
    vm: AuthViewModel = hiltViewModel()
) {
    val daftar by vm.setoranList.collectAsState()
    val status by vm.status.collectAsState()
    val errorMessage by vm.error.collectAsState()
    val ringkasan by vm.ringkasanSetoran.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()
    val colors = MaterialTheme.colorScheme

    val totalWajibSetor = ringkasan.sumOf { it.total_wajib_setor }
    val totalSudahSetor = ringkasan.sumOf { it.total_sudah_setor }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { vm.refreshSetoranList() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Text(
                        text = "Progress Muroja'ah",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primary
                    )
                }

                item {
                    when {
                        status == LoadingStatus.LOADING -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = colors.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        errorMessage.isNotBlank() -> {
                            Text(
                                text = "Error: $errorMessage",
                                color = colors.error,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        daftar.isEmpty() -> {
                            Text(
                                text = "Belum ada data setoran.",
                                fontSize = 18.sp,
                                color = colors.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> {
                            StatistikPieChart(daftar)
                        }
                    }
                }

                if (status != LoadingStatus.LOADING && errorMessage.isBlank() && daftar.isNotEmpty()) {
                    item {
                        RingkasanStatistik(
                            total = totalWajibSetor,
                            sudah = totalSudahSetor
                        )
                    }

                    item {
                        Text(
                            text = "Progress Kategori",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = colors.primary
                        )
                    }

                    items(ringkasan) { item ->
                        RingkasanItemCard(item)
                    }
                }
            }
        }
    }
}


@Composable
fun StatistikPieChart(daftar: List<Setoran>) {
    val totalSetoran = daftar.size
    val sudahSetor = daftar.count { it.sudah_setor }
    val belumSetor = totalSetoran - sudahSetor
    val persentaseDisplay = if (totalSetoran > 0) (sudahSetor * 100) / totalSetoran else 0

    val values = listOf(sudahSetor.toFloat(), belumSetor.toFloat())
    val totalValues = values.sum()

    val warnaSudahSetor = Color(0xFF4CAF50)
    val warnaBelumSetor = Color(0xFFF44336)

    val targetSweepAngles = values.map { (it / totalValues) * 360f }
    val animatedSweepAngles = targetSweepAngles.map { target ->
        animateFloatAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 800)
        ).value
    }

    Box(
        modifier = Modifier
            .size(220.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            var startAngle = -90f
            animatedSweepAngles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = if (index == 0) warnaSudahSetor else warnaBelumSetor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Text(
            text = "$persentaseDisplay%",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ColorLegendBox(color = warnaSudahSetor, label = "Sudah Setor")
        ColorLegendBox(color = warnaBelumSetor, label = "Belum Setor")
    }
}

@Composable
fun RingkasanStatistik(total: Int, sudah: Int) {
    val belum = total - sudah
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Statistik Umum",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            StatisticRow("Total Setoran", total.toString(), colors.primary)
            StatisticRow("Sudah Setor", sudah.toString(), Color(0xFF4CAF50))
            StatisticRow("Belum Setor", belum.toString(), Color(0xFFF44336))
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun RingkasanItemCard(item: RingkasanSetoran) {
    Log.d("DEBUG_RINGKASAN", "Label: ${item.label}, Progress: ${item.persentase_progres_setor}")
    val targetProgress = item.persentase_progres_setor / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress.toFloat(),
        animationSpec = tween(durationMillis = 700), label = ""
    )

    val label = item.label?.replace("_", " ") ?: "Tidak diketahui"
    val isCompleted = item.persentase_progres_setor == 100.toDouble()
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface,
                        fontSize = 16.sp
                    )
                }

                if (isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tuntas",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                } else {
                    Text(
                        text = "~ ${item.persentase_progres_setor}%",
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Wajib: ${item.total_wajib_setor}, Sudah: ${item.total_sudah_setor}, Belum: ${item.total_belum_setor}",
                fontSize = 13.sp,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = if (isCompleted) Color(0xFF66BB6A) else colors.primary,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${item.total_sudah_setor}/${item.total_wajib_setor}",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun ColorLegendBox(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
