package com.example.setoranhapalanmahasiswa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.setoranhapalanmahasiswa.model.LoginResponse
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.network.ApiClient
import com.example.setoranhapalanmahasiswa.network.getSetoranListFromApi
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

// State untuk menandakan status pengambilan data
enum class LoadingStatus {
    LOADING,
    SUCCESS,
    ERROR
}

class AuthViewModel : ViewModel() {
    var token by mutableStateOf("")
    var nama by mutableStateOf("")
    var error by mutableStateOf("")
    var status by mutableStateOf(LoadingStatus.SUCCESS)
    var setoranList = mutableStateListOf<Setoran>()

    suspend fun login(nim: String) {
        status = LoadingStatus.LOADING
        try {
            token = loginMahasiswa(nim)
            error = ""  // Reset error jika login berhasil
            status = LoadingStatus.SUCCESS  // Status sukses sebelum mengambil data
            println("Token berhasil didapatkan: $token")
            fetchSetoranList()
        } catch (e: Exception) {
            error = "Login gagal: ${e.message}"
            status = LoadingStatus.ERROR
            println("Error saat login: ${e.message}")
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
                }
            ) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                }
            }

            println("Status respons: ${response.status.value}")
            val responseBody = response.bodyAsText()
            println("Isi respons: $responseBody")  // Log respons mentah

            if (response.status.value != 200) {
                throw Exception("Login gagal: $responseBody")
            }

            // Parsing respons dengan opsi ignoreUnknownKeys
            // Parsing respons dengan opsi ignoreUnknownKeys
            try {
                val json = Json { ignoreUnknownKeys = true }  // Mengabaikan field yang tidak dikenal
                val body = json.decodeFromString<LoginResponse>(responseBody)
                println("Nama: ${body.name}, Token: ${body.accessToken}")  // Menggunakan nama field yang benar
                nama = body.name ?: "User"
                return body.accessToken
            } catch (e: Exception) {
                println("Error parsing JSON: ${e.message}")
                throw Exception("Gagal memproses respons: ${e.message}")
            }


        } catch (e: Exception) {
            println("Kesalahan login: ${e.message}")
            throw Exception("Kesalahan login: ${e.message}")
        }
    }




    suspend fun fetchSetoranList() {
        status = LoadingStatus.LOADING
        try {
            println("Mengambil daftar setoran dari API...")
            val response = getSetoranListFromApi(token)
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

    suspend fun getVerifikasi(id: Int) {
        // Logika untuk mengambil penilaian verifikasi jika diperlukan
        }
    }
