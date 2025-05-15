package com.example.setoranhapalanmahasiswa.model

@kotlinx.serialization.Serializable
data class SetoranResponse(
    val response: Boolean,       // Ubah dari status
    val message: String,
    val data: SetoranDataWrapper
)

@kotlinx.serialization.Serializable
data class SetoranDataWrapper(
    val setoran: SetoranDetailWrapper  // Ubah dari setorans
)

@kotlinx.serialization.Serializable
data class SetoranDetailWrapper(
    val detail: List<Setoran>
)

@kotlinx.serialization.Serializable
data class Setoran(
    val id: String,
    val nama: String,        // Sudah sesuai
    val nama_arab: String,   // Sudah sesuai
    val label: String,
    val sudah_setor: Boolean,
    val info_setoran: InfoSetoran? = null
)

@kotlinx.serialization.Serializable
data class InfoSetoran(
    val id: String? = null,
    val tgl_setoran: String? = null,
    val tgl_validasi: String? = null,
    val dosen_yang_mengesahkan: Dosen? = null
)

@kotlinx.serialization.Serializable
data class Dosen(
    val nip: String? = null,
    val nama: String? = null,
    val email: String? = null
)
