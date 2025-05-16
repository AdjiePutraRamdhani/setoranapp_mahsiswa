package com.example.setoranhapalanmahasiswa.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val sub: String = "",
    val name: String = "",
    val preferred_username: String = "",
    val email: String = "",
    val angkatan: String = "",
    val semester: Int = 0,
    val dosen_pa: DosenPA = DosenPA()
)

@Serializable
data class DosenPA(
    val nama: String = "",
    val nip: String = "",
    val email: String = ""
)

