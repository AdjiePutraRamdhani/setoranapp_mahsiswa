package com.example.setoranhapalanmahasiswa.model

@kotlinx.serialization.Serializable
data class SetoranResponse(
    val status: Boolean,
    val message: String,
    val data: SetoranDataWrapper
)

@kotlinx.serialization.Serializable
data class SetoranDataWrapper(
    val setoran: SetoranDetailWrapper
)

@kotlinx.serialization.Serializable
data class SetoranDetailWrapper(
    val detail: List<Setoran>
)

@kotlinx.serialization.Serializable
data class Setoran(
    val id: String,
    val surah: String,
    val ayat: String,
    val label: String,
    val sudah_setor: Boolean
)
