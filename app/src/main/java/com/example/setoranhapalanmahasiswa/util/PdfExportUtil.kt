package com.example.setoranhapalanmahasiswa.util

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.setoranhapalanmahasiswa.R
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.model.UserInfo
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Mengekspor daftar setoran ke PDF “Kartu Muroja’ah”.
 */
fun exportSetoranToPdf(
    context: Context,
    daftar: List<Setoran>,
    mahasiswa: UserInfo
) {
    val pdfDocument = PdfDocument()
    val pageWidth = 842
    val pageHeight = 1191
    val marginTop = 40f
    val lineHeight = 20f

    /* ===== Paint styles ===== */
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

    /* ===== Tabel ===== */
    val columnWidths = listOf(30f, 130f, 160f, 170f, 190f)
    val tableWidth = columnWidths.sum()
    val tableStartX = (pageWidth - tableWidth) / 2f
    val columns = buildList {
        var x = tableStartX
        add(x)
        columnWidths.forEach { w ->
            x += w
            add(x)
        }
    }

    /* ===== Halaman ===== */
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    var y = marginTop

    /* Logo & kop */
    val logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo_uin)
    val logoSize = 60f
    canvas.drawBitmap(
        logo, null, RectF(tableStartX, y, tableStartX + logoSize, y + logoSize), null
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

    /* Biodata */
    y += 80f
    canvas.drawText("Nama: ${mahasiswa.name}", tableStartX, y, titlePaint)
    y += lineHeight
    canvas.drawText("NIM   : ${mahasiswa.preferred_username}", tableStartX, y, titlePaint)
    y += lineHeight
    canvas.drawText("Pembimbing Akademik : ${mahasiswa.dosen_pa.nama}", tableStartX, y, titlePaint)
    y += lineHeight

    /* Header tabel */
    val headerHeight = lineHeight + 6f
    canvas.drawRect(columns.first(), y, columns.last(), y + headerHeight, headerBgPaint)

    fun centeredText(text: String, left: Float, right: Float, base: Float, p: Paint) {
        canvas.drawText(text, (left + right) / 2f, base, p)
    }

    val headers = listOf("No", "Surah", "Tanggal Muroja’ah", "Persyaratan", "Dosen")
    headers.indices.forEach { i ->
        centeredText(headers[i], columns[i], columns[i + 1], y + lineHeight, headerTextPaint)
    }

    columns.forEach { x -> canvas.drawLine(x, y, x, y + headerHeight, linePaint) }
    canvas.drawLine(columns.first(), y + headerHeight, columns.last(), y + headerHeight, linePaint)
    y += headerHeight

    /* Baris data */
    daftar.forEachIndexed { idx, s ->
        val rowHeight = lineHeight + 2f
        columns.forEach { x -> canvas.drawLine(x, y, x, y + rowHeight, linePaint) }
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

    /* Tanda tangan */
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

    /* ===== Simpan ke Download ===== */
    val fileName = "Kartu_Murojaah_${mahasiswa.name.replace(" ", "")}${System.currentTimeMillis()}.pdf"
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
    }

    try {
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            resolver.openOutputStream(it)?.use { pdfDocument.writeTo(it) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            Toast.makeText(context, "PDF berhasil disimpan", Toast.LENGTH_LONG).show()
        } ?: Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Terjadi kesalahan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}
