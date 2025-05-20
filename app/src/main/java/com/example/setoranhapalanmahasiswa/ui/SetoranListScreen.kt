package com.example.setoranhapalanmahasiswa.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.setoranhapalanmahasiswa.R
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.model.UserInfo
import com.example.setoranhapalanmahasiswa.viewmodel.AuthViewModel
import com.example.setoranhapalanmahasiswa.viewmodel.LoadingStatus
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
                    userInfo?.let {
                        exportSetoranToPdf(context, daftar, it)
                    } ?: Toast.makeText(context, "Data pengguna belum tersedia", Toast.LENGTH_SHORT).show()
                }
            ) {
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    items(daftar) { setoran ->
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

fun exportSetoranToPdf(context: Context, daftar: List<Setoran>, mahasiswa: UserInfo) {
    val pdfDocument = PdfDocument()
    val pageWidth = 842            // ukuran A4 landscape (≈ 297 mm)
    val pageHeight = 1191          // ukuran A4 portrait  (≈ 210 mm)
    val marginTop = 40f
    val lineHeight = 20f

    /* ---------- Paint ---------- */
    val bodyPaint = Paint().apply {
        textSize = 12f
        textAlign = Paint.Align.CENTER
    }
    val boldPaint = Paint().apply {
        textSize = 12f
        isFakeBoldText = true
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
    }
    val titlePaint = Paint().apply {
        textSize = 12f
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
    }
    val bigTitlePaint = Paint().apply {
        textSize = 14f
        isFakeBoldText = true
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
    }
    val linePaint = Paint().apply {
        strokeWidth = 1f
        color = Color.BLACK
    }
    val headerTextPaint = Paint().apply {
        textSize = 12f
        isFakeBoldText = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    val headerBgPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    /* ---------- Ukuran kolom ---------- */
    val columnWidths = listOf(30f, 130f, 160f, 170f, 190f)             // total: 680f
    val tableWidth = columnWidths.sum()
    val tableStartX =
        (pageWidth - tableWidth) / 2f                    // posisi X agar tabel di tengah

    /* ---------- Hitung posisi kolom ---------- */
    val columns = mutableListOf<Float>()
    var currentX = tableStartX
    columns.add(currentX)
    columnWidths.forEach { width ->
        currentX += width
        columns.add(currentX)
    }

    /* ---------- Mulai halaman ---------- */
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    var y = marginTop

    /* ---------- Kop surat ---------- */
    val logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_uin)
    val logoSize = 60f
    canvas.drawBitmap(
        logo,
        null,
        RectF(tableStartX, y, tableStartX + logoSize, y + logoSize),
        null
    )

    val kopX = tableStartX + logoSize + 12f
    canvas.drawText("KARTU MUROJA'AH JUZ 30", kopX, y + 12f, bigTitlePaint)
    canvas.drawText("PROGRAM STUDI TEKNIK INFORMATIKA", kopX, y + 28f, titlePaint)
    canvas.drawText("FAKULTAS SAINS DAN TEKNOLOGI", kopX, y + 44f, titlePaint)
    canvas.drawText(
        "UNIVERSITAS ISLAM NEGERI SULTAN SYARIF KASIM RIAU",
        kopX,
        y + 60f,
        titlePaint
    )

    /* ---------- Data mahasiswa ---------- */
    y += 80f
    canvas.drawText("Nama: ${mahasiswa.name}", tableStartX, y, titlePaint)
    y += lineHeight
    canvas.drawText(
        "NIM   : ${mahasiswa.preferred_username}",
        tableStartX,
        y,
        titlePaint
    )
    y += lineHeight
    canvas.drawText("Pembimbing Akademik : ${mahasiswa.dosen_pa.nama}", tableStartX, y, titlePaint)
    y += lineHeight

    /* ---------- Header tabel ---------- */
    val headerHeight = lineHeight + 6f
    canvas.drawRect(columns.first(), y, columns.last(), y + headerHeight, headerBgPaint)

    fun centeredText(text: String, left: Float, right: Float, baseline: Float, paint: Paint) {
        canvas.drawText(text, (left + right) / 2f, baseline, paint)
    }

    val headers = listOf("No", "Surah", "Tanggal Muroja’ah", "Persyaratan", "Dosen")
    headers.indices.forEach { i ->
        centeredText(headers[i], columns[i], columns[i + 1], y + lineHeight, headerTextPaint)
    }

    columns.forEach { x ->
        canvas.drawLine(x, y, x, y + headerHeight, linePaint)
    }
    canvas.drawLine(columns.first(), y + headerHeight, columns.last(), y + headerHeight, linePaint)

    /* ---------- Baris data ---------- */
    y += headerHeight
    daftar.forEachIndexed { idx, s ->
        val rowHeight = lineHeight + 2f
        columns.forEach { x ->
            canvas.drawLine(x, y, x, y + rowHeight, linePaint)
        }
        canvas.drawLine(columns.first(), y + rowHeight, columns.last(), y + rowHeight, linePaint)

        val values = listOf(
            (idx + 1).toString(),
            s.nama.take(30),
            s.info_setoran?.tgl_setoran?.take(30) ?: "-",
            s.label.ifBlank { "-" }.take(30),
            s.info_setoran?.dosen_yang_mengesahkan?.nama?.take(30) ?: "-"
        )
        values.indices.forEach { i ->
            centeredText(values[i], columns[i], columns[i + 1], y + lineHeight, bodyPaint)
        }
        y += rowHeight
    }

    /* ---------- Tanda tangan ---------- */
    val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
    y = pageHeight - marginTop - 100f
    val ttdX = columns.last() - 200f

    canvas.drawText("Pekanbaru, $currentDate", ttdX, y, titlePaint)
    y += lineHeight
    canvas.drawText("Pembimbing Akademik,", ttdX, y, titlePaint)
    y += lineHeight * 4f
    canvas.drawText(mahasiswa.dosen_pa.nama, ttdX, y, boldPaint)
    y += lineHeight
    canvas.drawText("NIP. ${mahasiswa.dosen_pa.nip}", ttdX, y, titlePaint)

    pdfDocument.finishPage(page)

    /* ---------- Simpan PDF ---------- */
    val fileName =
        "Kartu_Murojaah_${mahasiswa.name.replace(" ", "")}${System.currentTimeMillis()}.pdf"
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
    }
    val resolver = context.contentResolver
    resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)?.let { uri ->
        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            Toast.makeText(context, "Berhasil simpan PDF di folder Download", Toast.LENGTH_SHORT)
                .show()
        } catch (e: IOException) {
            Toast.makeText(context, "Gagal simpan PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    } ?: run {
        Toast.makeText(context, "Gagal membuat file PDF", Toast.LENGTH_LONG).show()
    }

    pdfDocument.close()
}