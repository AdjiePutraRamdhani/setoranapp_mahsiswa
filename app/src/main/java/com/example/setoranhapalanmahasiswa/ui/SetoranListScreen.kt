package com.example.setoranhapalanmahasiswa.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
import androidx.compose.runtime.*
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
fun SetoranListScreen(nav: NavHostController, vm: AuthViewModel = hiltViewModel()) {
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
    val pageWidth = 595
    val pageHeight = 842
    val margin = 40
    val lineHeight = 24f

    val paint = Paint().apply { textSize = 12f }
    val boldPaint = Paint().apply {
        textSize = 12f
        isFakeBoldText = true
        color = Color.BLACK
    }
    val titlePaint = Paint().apply {
        textSize = 14f
        isFakeBoldText = true
        color = Color.BLACK
    }
    val bigTitlePaint = Paint().apply {
        textSize = 16f
        isFakeBoldText = true
        color = Color.BLACK
    }
    val linePaint = Paint().apply {
        strokeWidth = 1.2f
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

    val columns = listOf(
        margin.toFloat(),
        margin + 30f,
        margin + 130f,
        margin + 250f,
        margin + 370f
    )

    var pageIndex = 0
    var y = margin + 30f

    fun startNewPage(): Pair<PdfDocument.Page, Canvas> {
        pageIndex++
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create()
        val page = pdfDocument.startPage(pageInfo)
        return page to page.canvas
    }

    fun drawCenteredText(canvas: Canvas, text: String, left: Float, right: Float, baseline: Float, paint: Paint) {
        val centerX = (left + right) / 2
        val textWidth = paint.measureText(text)
        canvas.drawText(text, centerX - textWidth / 2, baseline, paint)
    }

    fun drawHeader(canvas: Canvas) {
        y = margin.toFloat()

        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_uin)
        canvas.drawBitmap(logo, null, Rect(margin, y.toInt(), margin + 60, y.toInt() + 60), null)

        canvas.drawText("KARTU MUROJA'AH JUZ 30", margin + 80f, y + 10f, bigTitlePaint)
        canvas.drawText("PROGRAM STUDI TEKNIK INFORMATIKA", margin + 80f, y + 30f, titlePaint)
        canvas.drawText("FAKULTAS SAINS DAN TEKNOLOGI", margin + 80f, y + 45f, titlePaint)
        canvas.drawText("UNIVERSITAS ISLAM NEGERI SULTAN SYARIF KASIM RIAU", margin + 80f, y + 60f, titlePaint)

        y += 80f
        canvas.drawText("Nama                : ${mahasiswa.name}", margin.toFloat(), y, paint)
        y += lineHeight
        canvas.drawText("NIM                 : ${mahasiswa.preferred_username}", margin.toFloat(), y, paint)
        y += lineHeight
        canvas.drawText("Pembimbing Akademik: ${mahasiswa.dosen_pa.nama}", margin.toFloat(), y, paint)

        y += lineHeight

        val headerHeight = lineHeight + 6f
        canvas.drawRect(margin.toFloat(), y, pageWidth - margin.toFloat(), y + headerHeight, headerBgPaint)

        drawCenteredText(canvas, "No", columns[0], columns[1], y + lineHeight, headerTextPaint)
        drawCenteredText(canvas, "Surah", columns[1], columns[2], y + lineHeight, headerTextPaint)
        drawCenteredText(canvas, "Tanggal Muroja’ah", columns[2], columns[3], y + lineHeight, headerTextPaint)
        drawCenteredText(canvas, "Persyaratan", columns[3], columns[4], y + lineHeight, headerTextPaint)
        drawCenteredText(canvas, "Dosen", columns[4], pageWidth - margin.toFloat(), y + lineHeight, headerTextPaint)

        columns.forEach { x -> canvas.drawLine(x, y, x, y + headerHeight, linePaint) }
        canvas.drawLine(pageWidth - margin.toFloat(), y, pageWidth - margin.toFloat(), y + headerHeight, linePaint)
        canvas.drawLine(margin.toFloat(), y + headerHeight, pageWidth - margin.toFloat(), y + headerHeight, linePaint)

        y += headerHeight
    }

    var (page, canvas) = startNewPage()
    drawHeader(canvas)

    daftar.forEachIndexed { index, s ->
        if (y > pageHeight - margin - lineHeight * 5) {
            pdfDocument.finishPage(page)
            val newPage = startNewPage()
            page = newPage.first
            canvas = newPage.second
            drawHeader(canvas)
        }

        val tglSetor = s.info_setoran?.tgl_setoran ?: "-"
        val dosen = s.info_setoran?.dosen_yang_mengesahkan?.nama ?: "-"
        val persyaratan = s.label.ifBlank { "-" }

        val cellHeight = lineHeight + 4f

        canvas.drawLine(margin.toFloat(), y, pageWidth - margin.toFloat(), y, linePaint)
        columns.forEach { x -> canvas.drawLine(x, y, x, y + cellHeight, linePaint) }
        canvas.drawLine(pageWidth - margin.toFloat(), y, pageWidth - margin.toFloat(), y + cellHeight, linePaint)

        drawCenteredText(canvas, "${index + 1}", columns[0], columns[1], y + lineHeight, paint)
        drawCenteredText(canvas, s.nama.take(20), columns[1], columns[2], y + lineHeight, paint)
        drawCenteredText(canvas, tglSetor.take(20), columns[2], columns[3], y + lineHeight, paint)
        drawCenteredText(canvas, persyaratan.take(20), columns[3], columns[4], y + lineHeight, paint)
        drawCenteredText(canvas, dosen.take(25), columns[4], pageWidth - margin.toFloat(), y + lineHeight, paint)

        y += cellHeight
    }

    val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
    y = pageHeight - margin - 60f
    canvas.drawText("Pekanbaru, $currentDate", pageWidth - 240f, y, paint)
    y += lineHeight
    canvas.drawText("Pembimbing Akademik,", pageWidth - 240f, y, paint)
    y += lineHeight * 4
    canvas.drawText(mahasiswa.dosen_pa.nama, pageWidth - 240f, y, boldPaint)
    canvas.drawText("NIP. ${mahasiswa.dosen_pa.nip}", pageWidth - 240f, y + 16f, paint)

    pdfDocument.finishPage(page)

    val fileName = "Kartu_Murojaah_${mahasiswa.name.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        try {
            resolver.openOutputStream(uri)?.use { out ->
                pdfDocument.writeTo(out)
                Toast.makeText(context, "PDF berhasil disimpan di folder Download.", Toast.LENGTH_LONG).show()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    pdfDocument.close()
}

