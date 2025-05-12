package com.example.setoranhapalanmahasiswa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("refresh_expires_in") val refreshExpiresIn: Int,
    @SerialName("not-before-policy") val notBeforePolicy: Int,   // Mengatasi nama dengan tanda hubung
    @SerialName("session_state") val sessionState: String,
    @SerialName("scope") val scope: String,
    @SerialName("name") val name: String? = null,
    @SerialName("preferred_username") val preferredUsername: String? = null,
    @SerialName("given_name") val givenName: String? = null,
    @SerialName("family_name") val familyName: String? = null,
    @SerialName("email") val email: String? = null
)


