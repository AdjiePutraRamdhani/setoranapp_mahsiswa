package com.example.setoranhapalanmahasiswa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.setoranhapalanmahasiswa.model.LoginRequest
import com.example.setoranhapalanmahasiswa.model.LoginResponse
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.network.ApiClient
import com.example.setoranhapalanmahasiswa.network.getSetoranListFromApi
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType

// State untuk menandakan status pengambilan data
enum class LoadingStatus {
    LOADING,
    SUCCESS,
    ERROR
}

class AuthViewModel : ViewModel() {
    // State untuk menyimpan token, nama mahasiswa, dan pesan error
    var token by mutableStateOf("")
    var nama by mutableStateOf("")
    var error by mutableStateOf("")
    var status by mutableStateOf(LoadingStatus.SUCCESS)

    // Fungsi login untuk mahasiswa, diletakkan langsung dalam ViewModel
    suspend fun login(nim: String) {
        status = LoadingStatus.LOADING // Menandakan proses loading saat login
        try {
            token = loginMahasiswa(nim) // Mendapatkan token setelah login
            // Opsional: Anda bisa menambahkan logika untuk decode token jika diperlukan
            status = LoadingStatus.SUCCESS // Menandakan login sukses
        } catch (e: Exception) {
            error = "Login gagal: ${e.message}" // Menyimpan pesan error jika gagal login
            status = LoadingStatus.ERROR // Menandakan terjadi error saat login
        }
    }

    // Fungsi login yang memanggil API untuk login
    suspend fun loginMahasiswa(nim: String): String {
        if (nim.isBlank()) throw Exception("NIM tidak boleh kosong")

        try {
            println("Sending request to: https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/token")

            val response = ApiClient.client.submitForm(
                url = "https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/token",
                formParameters = Parameters.build {
                    append("grant_type", "password")
                    append("client_id", "setoran-mobile-dev") // ganti sesuai konfigurasi Anda
                    append("client_secret", "aqJp3xnXKudgC7RMOshEQP7ZoVKWzoSl") // jika client menggunakan secret
                    append("username", nim)
                    append("password", nim)
                }
            ) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                }
            }

            println("Response status: ${response.status.value}")
            val responseBody = response.bodyAsText()
            println("Response body: $responseBody")

            if (response.status.value != 200) {
                throw Exception("Login gagal: $responseBody")
            }

            val body = response.body<LoginResponse>()
            return body.access_token

        } catch (e: Exception) {
            println("Error during login: ${e.message}")
            throw Exception("Terjadi kesalahan: ${e.message}")
        }
    }









    // Fungsi untuk mengambil daftar setoran dari API
    suspend fun getSetoranList(): List<Setoran> {
        status = LoadingStatus.LOADING // Menandakan proses loading saat mengambil daftar setoran
        return try {
            // Mengambil daftar setoran menggunakan fungsi dari ApiService
            val response = getSetoranListFromApi(token)
            status = LoadingStatus.SUCCESS // Menandakan berhasil mengambil data
            response // Mengembalikan daftar setoran
        } catch (e: Exception) {
            error = "Gagal mengambil daftar setoran: ${e.message}" // Menyimpan pesan error jika gagal
            status = LoadingStatus.ERROR // Menandakan error saat mengambil data
            emptyList() // Mengembalikan list kosong jika terjadi error
        }
    }

    // Fungsi untuk mengirim setoran (POST request)
    suspend fun submitSetoran(surah: String, ayat: String, tanggal: String) {
        // Logika untuk mengirimkan setoran, jika diperlukan
    }

    // Fungsi untuk mendapatkan penilaian verifikasi
    suspend fun getVerifikasi(id: Int) {
        // Logika untuk mengambil penilaian verifikasi jika diperlukan
    }
}
