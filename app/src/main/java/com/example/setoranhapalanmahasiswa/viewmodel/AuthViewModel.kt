package com.example.setoranhapalanmahasiswa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.setoranhapalanmahasiswa.datastore.DataStoreManager
import com.example.setoranhapalanmahasiswa.model.LoginResponse
import com.example.setoranhapalanmahasiswa.model.RingkasanSetoran
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.model.UserInfo
import com.example.setoranhapalanmahasiswa.network.ApiClient
import com.example.setoranhapalanmahasiswa.network.getSetoranListFromApi
import com.example.setoranhapalanmahasiswa.network.getUserProfileFromApi
import com.example.setoranhapalanmahasiswa.network.getRingkasanSetoranFromApi
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

enum class LoadingStatus {
    LOADING, SUCCESS, ERROR
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var token = ""

    private val _nama = MutableStateFlow("Nama Tidak Diketahui")
    val nama: StateFlow<String> = _nama

    private val _email = MutableStateFlow("Email Tidak Diketahui")
    val email: StateFlow<String> = _email

    private val _nim = MutableStateFlow("NIM Tidak Diketahui")
    val nim: StateFlow<String> = _nim

    private val _angkatan = MutableStateFlow("Angkatan Tidak Diketahui")
    val angkatan: StateFlow<String> = _angkatan

    private val _semester = MutableStateFlow(0)
    val semester: StateFlow<Int> = _semester

    private val _dosenPaNama = MutableStateFlow("Nama Dosen Tidak Diketahui")
    val dosenPaNama: StateFlow<String> = _dosenPaNama

    private val _dosenPaEmail = MutableStateFlow("Email Dosen Tidak Diketahui")
    val dosenPaEmail: StateFlow<String> = _dosenPaEmail

    private val _dosenPaNip = MutableStateFlow("NIP Dosen Tidak Diketahui")
    val dosenPaNip: StateFlow<String> = _dosenPaNip

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _status = MutableStateFlow(LoadingStatus.SUCCESS)
    val status: StateFlow<LoadingStatus> = _status

    private val _setoranList = MutableStateFlow<List<Setoran>>(emptyList())
    val setoranList: StateFlow<List<Setoran>> = _setoranList

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    private val _ringkasanSetoran = MutableStateFlow<List<RingkasanSetoran>>(emptyList())
    val ringkasanSetoran: StateFlow<List<RingkasanSetoran>> = _ringkasanSetoran

    init {
        viewModelScope.launch {
            dataStoreManager.token.collect { savedToken ->
                token = savedToken ?: ""
                if (token.isNotEmpty()) {
                    try {
                        fetchUserInfo(token)
                        fetchSetoranList()
                        fetchRingkasanSetoran()
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Error during initialization: ${e.message}")
                        _error.value = "Gagal memuat data: ${e.message}"
                    }
                }
            }
        }
    }

    fun login(nim: String, password: String ) {
        viewModelScope.launch {
            if (password.isBlank()) {
                _error.value = "NIM tidak boleh kosong"
                return@launch
            }
            updateStatus(LoadingStatus.LOADING)
            try {
                val newToken = loginMahasiswa(nim, password)
                token = newToken
                _error.value = ""
                updateStatus(LoadingStatus.SUCCESS)
                fetchUserInfo(token)
                fetchSetoranList()
            } catch (e: Exception) {
                _error.value = "Login gagal: ${e.message}"
                updateStatus(LoadingStatus.ERROR)
            }
        }
    }

    private suspend fun loginMahasiswa(nim: String, password: String): String {
        val response = ApiClient.client.submitForm(
            url = "https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/token",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("client_id", "setoran-mobile-dev")
                append("client_secret", "aqJp3xnXKudgC7RMOshEQP7ZoVKWzoSl")
                append("username", nim)
                append("password", password)
                append("scope", "openid profile email")
            }
        ) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
        }

        val responseBody = response.bodyAsText()
        if (response.status.value != 200) throw Exception("Login gagal: $responseBody")

        val body = Json { ignoreUnknownKeys = true }.decodeFromString<LoginResponse>(responseBody)
        dataStoreManager.saveToken(body.accessToken)
        dataStoreManager.saveRefreshToken(body.refreshToken)
        return body.accessToken
    }

    fun fetchUserInfo(token: String) {
        viewModelScope.launch {
            updateStatus(LoadingStatus.LOADING)
            try {
                val profile = getUserProfileFromApi(token)
                _userInfo.value = profile

                _nama.value = profile?.name ?: "Nama Tidak Diketahui"
                _email.value = profile?.email ?: "Email Tidak Diketahui"
                _nim.value = profile?.preferred_username ?: "NIM Tidak Diketahui"
                _angkatan.value = profile?.angkatan ?: "Angkatan Tidak Diketahui"
                _semester.value = profile?.semester ?: 0
                _dosenPaNama.value = profile?.dosen_pa?.nama ?: "Nama Dosen Tidak Diketahui"
                _dosenPaEmail.value = profile?.dosen_pa?.email ?: "Email Dosen Tidak Diketahui"
                _dosenPaNip.value = profile?.dosen_pa?.nip ?: "NIP Dosen Tidak Diketahui"

                updateStatus(LoadingStatus.SUCCESS)
            } catch (e: Exception) {
                _error.value = "Gagal mengambil profil: ${e.message}"
                updateStatus(LoadingStatus.ERROR)
            }
        }
    }

    fun fetchSetoranList() {
        viewModelScope.launch {
            updateStatus(LoadingStatus.LOADING)
            try {
                val setoranData = getSetoranListFromApi(token)
                if (setoranData.isNullOrEmpty()) throw Exception("Data setoran tidak tersedia.")
                _setoranList.value = setoranData
                updateStatus(LoadingStatus.SUCCESS)
                Log.d("AuthViewModel", "Berhasil mengambil ${setoranData.size} data setoran")
            } catch (e: Exception) {
                _error.value = "Gagal mengambil daftar setoran: ${e.message}"
                updateStatus(LoadingStatus.ERROR)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            dataStoreManager.clearRefreshToken()
            dataStoreManager.clearUserProfile()
            resetUserData()
        }
    }

    private fun resetUserData() {
        token = ""
        _nama.value = "Nama Tidak Diketahui"
        _email.value = "Email Tidak Diketahui"
        _nim.value = "NIM Tidak Diketahui"
        _angkatan.value = "Angkatan Tidak Diketahui"
        _semester.value = 0
        _dosenPaNama.value = "Nama Dosen Tidak Diketahui"
        _dosenPaEmail.value = "Email Dosen Tidak Diketahui"
        _dosenPaNip.value = "NIP Dosen Tidak Diketahui"
        _error.value = ""
        _setoranList.value = emptyList()
        _userInfo.value = null
        updateStatus(LoadingStatus.SUCCESS)
    }

    private fun updateStatus(newStatus: LoadingStatus) {
        _status.value = newStatus
    }

    fun fetchRingkasanSetoran() {
        viewModelScope.launch {
            updateStatus(LoadingStatus.LOADING)
            try {
                val ringkasan = getRingkasanSetoranFromApi(token)
                if (ringkasan == null) {
                    _error.value = "Ringkasan tidak ditemukan."
                    updateStatus(LoadingStatus.ERROR)
                    return@launch
                }

                _ringkasanSetoran.value = ringkasan
                updateStatus(LoadingStatus.SUCCESS)
            } catch (e: Exception) {
                _error.value = "Gagal mengambil ringkasan: ${e.message}"
                updateStatus(LoadingStatus.ERROR)
            }
        }
    }

}
