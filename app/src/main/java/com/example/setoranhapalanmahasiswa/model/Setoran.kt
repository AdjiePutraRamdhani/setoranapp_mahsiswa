package com.example.setoranhapalanmahasiswa.model

data class Setoran(
    val id: String,
    val surah: String,
    val ayat: String,
    val label: String,
    val sudah_setor: Boolean
)
