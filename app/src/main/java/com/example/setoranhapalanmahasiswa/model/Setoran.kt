package com.example.setoranhapalanmahasiswa.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val response: Boolean,
    val message: String,
    val data: DataContent
)

@Serializable
data class DataContent(
    val info: UserInfo,
    val setoran: SetoranWrapper
)

@Serializable
data class SetoranWrapper(
    val log: List<LogItem>,
    val info_dasar: InfoDasar,
    val ringkasan: List<RingkasanSetoran>,
    val detail: List<Setoran>
)

@Serializable
data class LogItem(
    val id: Int,
    val keterangan: String,
    val aksi: String,
    val ip: String,
    val user_agent: String,
    val timestamp: String,
    val nim: String,
    val dosen_yang_mengesahkan: DosenPA
)

@Serializable
data class InfoDasar(
    val total_wajib_setor: Int,
    val total_sudah_setor: Int,
    val total_belum_setor: Int,
    val persentase_progres_setor: Double,
    val tgl_terakhir_setor: String,
    val terakhir_setor: String
)

@Serializable
data class RingkasanSetoran(
    val label: String,
    val total_wajib_setor: Int,
    val total_sudah_setor: Int,
    val total_belum_setor: Int,
    val persentase_progres_setor: Double
)

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
    val nip: String,
    val nama: String,
    val email: String
)
