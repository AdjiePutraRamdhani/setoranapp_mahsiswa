package com.example.setoranhapalanmahasiswa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.setoranhapalanmahasiswa.datastore.DataStoreManager
import com.example.setoranhapalanmahasiswa.model.LoginResponse
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.network.ApiClient
import com.example.setoranhapalanmahasiswa.network.getSetoranListFromApi
import com.example.setoranhapalanmahasiswa.network.getUserProfileFromApi
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

enum class LoadingStatus {
    LOADING,
    SUCCESS,
    ERROR
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var token by mutableStateOf("")
    var nama by mutableStateOf("Nama Tidak Diketahui")
    var email by mutableStateOf("Email Tidak Diketahui")
    var nim by mutableStateOf("NIM Tidak Diketahui")
    var error by mutableStateOf("")
    var status by mutableStateOf(LoadingStatus.SUCCESS)
    var setoranList = mutableStateListOf<Setoran>()

    init {
        viewModelScope.launch {
            dataStoreManager.token.collect { savedToken ->
                token = savedToken ?: ""
                if (token.isNotEmpty()) {
                    fetchUserInfo(token)
                }
            }
        }
    }

    fun login(nim: String) {
        viewModelScope.launch {
            status = LoadingStatus.LOADING
            try {
                token = loginMahasiswa(nim)
                error = ""
                status = LoadingStatus.SUCCESS

                fetchUserInfo(token)
                fetchSetoranList()
            } catch (e: Exception) {
                error = "Login gagal: ${e.message}"
                status = LoadingStatus.ERROR
                println("Error saat login: ${e.message}")
            }
        }
    }

    private suspend fun loginMahasiswa(nim: String): String {
        if (nim.isBlank()) throw Exception("NIM tidak boleh kosong")

        try {
            println("Mengirim permintaan login ke server...")

            val response = ApiClient.client.submitForm(
                url = "https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/token",
                formParameters = Parameters.build {
                    append("grant_type", "password")
                    append("client_id", "setoran-mobile-dev")
                    append("client_secret", "aqJp3xnXKudgC7RMOshEQP7ZoVKWzoSl")
                    append("username", nim)
                    append("password", nim)
                    append("scope", "openid profile email")
                }
            ) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                }
            }

            val responseBody = response.bodyAsText()
            if (response.status.value != 200) {
                throw Exception("Login gagal: $responseBody")
            }

            val body = Json { ignoreUnknownKeys = true }.decodeFromString<LoginResponse>(responseBody)

            dataStoreManager.saveToken(body.accessToken)
            dataStoreManager.saveRefreshToken(body.refreshToken)
            println("Token berhasil disimpan: ${body.accessToken}")
            println("Refresh Token berhasil disimpan: ${body.refreshToken}")

            return body.accessToken

        } catch (e: Exception) {
            throw Exception("Kesalahan login: ${e.message}")
        }
    }

    suspend fun refreshToken(): String? {
        try {
            println("Memperbarui token...")

            val refreshToken = dataStoreManager.getRefreshToken() ?: throw Exception("Refresh token tidak ditemukan")

            val response = ApiClient.client.submitForm(
                url = "https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/token",
                formParameters = Parameters.build {
                    append("grant_type", "refresh_token")
                    append("client_id", "setoran-mobile-dev")
                    append("refresh_token", refreshToken)
                }
            ) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                }
            }

            val responseBody = response.bodyAsText()
            if (response.status.value == 200) {
                val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                val newToken = jsonObject["access_token"]?.jsonPrimitive?.content

                if (newToken != null) {
                    println("Token berhasil diperbarui: $newToken")
                    dataStoreManager.saveToken(newToken)
                    token = newToken
                    return newToken
                } else {
                    throw Exception("Token baru tidak ditemukan dalam respons.")
                }
            } else {
                throw Exception("Gagal memperbarui token: ${response.status}")
            }
        } catch (e: Exception) {
            println("Error saat memperbarui token: ${e.message}")
            error = "Gagal memperbarui token: ${e.message}"
            return null
        }
    }

    suspend fun fetchUserInfo(token: String) {
        status = LoadingStatus.LOADING
        try {
            val profile = getUserProfileFromApi(token)

            if (profile != null) {
                nama = profile.name ?: "Nama Tidak Diketahui"
                email = profile.email ?: "Email Tidak Diketahui"
                nim = profile.preferred_username ?: "NIM Tidak Diketahui"
                status = LoadingStatus.SUCCESS
                println("Profil berhasil diambil: Nama: $nama, NIM: $nim, Email: $email")

                dataStoreManager.saveUserProfile(nama, email, nim)
            } else {
                throw Exception("Data profil kosong atau tidak ditemukan.")
            }
        } catch (e: Exception) {
            error = "Gagal mengambil profil: ${e.message}"
            status = LoadingStatus.ERROR
            println("Error saat mengambil profil: ${e.message}")
        }
    }

    suspend fun fetchSetoranList() {
        status = LoadingStatus.LOADING
        try {
            println("Mengambil daftar setoran dari API...")
            val response = getSetoranListFromApi()
            setoranList.clear()
            setoranList.addAll(response)
            status = LoadingStatus.SUCCESS
            println("Berhasil mengambil ${response.size} data setoran.")
        } catch (e: Exception) {
            error = "Gagal mengambil daftar setoran: ${e.message}"
            status = LoadingStatus.ERROR
            println("Error: ${e.message}")
        }
    }

    fun getSetoranList(): List<Setoran> {
        return setoranList
    }

    fun getVerifikasi(id: Int) {
        // Placeholder - isi nanti sesuai kebutuhan
    }

    // ✅ Fungsi Logout
    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            dataStoreManager.clearRefreshToken()
            dataStoreManager.clearUserProfile()

            token = ""
            nama = "Nama Tidak Diketahui"
            email = "Email Tidak Diketahui"
            nim = "NIM Tidak Diketahui"
            error = ""
            status = LoadingStatus.SUCCESS
            setoranList.clear()

            println("Logout berhasil, semua data direset.")
        }
    }
}
