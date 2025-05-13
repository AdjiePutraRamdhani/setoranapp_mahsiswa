package com.example.setoranhapalanmahasiswa.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val sub: String? = null,                 // Bisa null jika tidak ada
    val name: String? = null,                // Bisa null jika tidak ada
    val preferred_username: String? = null,  // Bisa null jika tidak ada
    val email: String? = null                // Bisa null jika tidak ada
)
