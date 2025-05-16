package com.example.setoranhapalanmahasiswa.model

import kotlinx.serialization.Serializable

@Serializable
data class Setoran(
    val id: String,
    val nama: String,
    val nama_arab: String,
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

@Serializable
data class RingkasanSetoran(
    val total_wajib_setor: Int = 0,
    val total_sudah_setor: Int = 0,
    val total_belum_setor: Int = 0,
    val persentase_progres_setor: Int = 0
)
