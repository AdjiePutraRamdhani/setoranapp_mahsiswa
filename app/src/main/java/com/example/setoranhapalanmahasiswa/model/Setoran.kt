package com.example.setoranhapalanmahasiswa.model

import kotlinx.serialization.Serializable

@Serializable
data class Setoran(
    val id: String,
    val nama: String,        // Sudah sesuai
    val nama_arab: String,   // Sudah sesuai
    val label: String,
    val sudah_setor: Boolean,
    val info_setoran: InfoSetoran? = null
)

@Serializable
data class InfoSetoran(
    val id: String? = null,
    val tgl_setoran: String? = null,
    val tgl_validasi: String? = null,
    val dosen_yang_mengesahkan: Dosen? = null
)

@Serializable
data class Dosen(
    val nip: String? = null,
    val nama: String? = null,
    val email: String? = null
)
