package com.example.setoranhapalanmahasiswa.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.setoranhapalanmahasiswa.R
import androidx.compose.foundation.Image // <-- Import ini untuk Image
import androidx.compose.ui.res.painterResource // <-- Import ini untuk painterResource
import androidx.compose.ui.layout.ContentScale // <-- Import ini untuk ContentScale
import androidx.compose.ui.graphics.Color // <-- Import ini untuk Color jika pakai overlay warna

@Composable
fun LoadingScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val alphaAnim = rememberInfiniteTransition()
    val animatedAlpha by alphaAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- MULAI PENAMBAHAN GAMBAR BACKGROUND ---
        Image(
            painter = painterResource(id = R.drawable.sabarimage), // <--- GANTI DENGAN NAMA FILE GAMBAR ANDA DI res/drawable
            contentDescription = "Background Loading Screen", // Tambahkan deskripsi yang relevan
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Memotong gambar agar mengisi seluruh area
        )

        // Opsional: Tambahkan overlay untuk membuat teks dan animasi lebih mudah dibaca
        // Ini akan membuat gambar background sedikit lebih redup
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)) // Overlay hitam transparan
            // Anda juga bisa mencoba overlay dengan warna MaterialTheme Anda:
            // .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
        )
        // --- AKHIR PENAMBAHAN GAMBAR BACKGROUND ---

        // Konten utama Loading Screen (Lottie, teks, dll.)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(180.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Mohon tunggu sebentar \uD83D\uDE4F",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White // Ubah warna teks agar kontras dengan background gelap
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Memuat data hafalan...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.7f), // Ubah warna teks agar kontras
                    modifier = Modifier.alpha(animatedAlpha)
                )
            }
        }
    }
}