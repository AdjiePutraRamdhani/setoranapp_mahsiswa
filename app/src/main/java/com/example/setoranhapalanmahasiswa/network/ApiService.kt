package com.example.setoranhapalanmahasiswa.network

import android.util.Log
import com.example.setoranhapalanmahasiswa.datastore.DataStoreManager
import com.example.setoranhapalanmahasiswa.model.Dosen
import com.example.setoranhapalanmahasiswa.model.InfoSetoran
import com.example.setoranhapalanmahasiswa.model.Setoran
import com.example.setoranhapalanmahasiswa.model.UserInfo
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonNull

// Fungsi untuk mengambil data setoran mahasiswa
// Fungsi untuk mengambil data setoran mahasiswa
suspend fun getSetoranListFromApi(token: String): List<Setoran> = withContext(Dispatchers.IO) {
    try {
        // Melakukan request ke API setoran
        val response: HttpResponse = ApiClient.client.get("https://api.tif.uin-suska.ac.id/setoran-dev/v1/mahasiswa/setoran-saya") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        // Parsing respons menjadi JsonObject
        val fullResponse = response.body<JsonObject>()
        Log.d("API Response", "Full response: $fullResponse")

        if (fullResponse.isEmpty()) {
            Log.e("API Response", "Respons kosong atau tidak sesuai format!")
            return@withContext emptyList()
        }

        // Mengambil array detail setoran
        val detailArray = fullResponse["data"]?.jsonObject
            ?.get("setoran")?.jsonObject
            ?.get("detail")?.jsonArray

        if (detailArray == null || detailArray.isEmpty()) {
            Log.e("API Response", "Detail array kosong atau tidak ditemukan")
            return@withContext emptyList()
        }

        Log.d("API Response", "Jumlah data yang didapat: ${detailArray.size}")

        // Mapping JSON menjadi objek Setoran
        val setoranList = detailArray.map { jsonElement ->
            val jsonObject = jsonElement.jsonObject
            Log.d("API Parsing", "Item: $jsonObject")

            // Cek apakah info_setoran tidak null
            // Cek apakah info_setoran tidak null atau kosong
            val infoSetoran = jsonObject["info_setoran"]?.takeIf { it !is JsonNull }?.jsonObject?.let {
                InfoSetoran(
                    id = it["id"]?.jsonPrimitive?.content,
                    tgl_setoran = it["tgl_setoran"]?.jsonPrimitive?.content,
                    tgl_validasi = it["tgl_validasi"]?.jsonPrimitive?.content,
                    dosen_yang_mengesahkan = it["dosen_yang_mengesahkan"]?.jsonObject?.let { dosen ->
                        Dosen(
                            nip = dosen["nip"]?.jsonPrimitive?.content,
                            nama = dosen["nama"]?.jsonPrimitive?.content,
                            email = dosen["email"]?.jsonPrimitive?.content
                        )
                    }
                )
            }


            Setoran(
                id = jsonObject["id"]?.jsonPrimitive?.content ?: "",
                nama = jsonObject["nama"]?.jsonPrimitive?.content ?: "",
                nama_arab = jsonObject["nama_arab"]?.jsonPrimitive?.content ?: "",
                label = jsonObject["label"]?.jsonPrimitive?.content ?: "",
                sudah_setor = jsonObject["sudah_setor"]?.jsonPrimitive?.boolean ?: false,
                info_setoran = infoSetoran
            )
        }

        Log.d("API Response", "Berhasil mengambil ${setoranList.size} data setoran")
        return@withContext setoranList
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("API Response", "Gagal mengambil data: ${e.message}")
        return@withContext emptyList()
    }
}




// Fungsi untuk mengambil data profil mahasiswa dari API Keycloak
suspend fun getUserProfileFromApi(token: String): UserInfo? = withContext(Dispatchers.IO) {
    try {
        val response: HttpResponse = ApiClient.client.get("https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/userinfo") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        val fullResponse = response.body<JsonObject>()
        Log.d("API Response", "Profil Mahasiswa: $fullResponse")

        return@withContext UserInfo(
            sub = fullResponse["sub"]?.jsonPrimitive?.content ?: "",
            name = fullResponse["name"]?.jsonPrimitive?.content ?: "",
            preferred_username = fullResponse["preferred_username"]?.jsonPrimitive?.content ?: "",
            email = fullResponse["email"]?.jsonPrimitive?.content ?: ""
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("API Response", "Gagal mengambil data profil: ${e.message}")
        return@withContext null
    }
}
