package com.example.setoranhapalanmahasiswa.network

import android.util.Log
import com.example.setoranhapalanmahasiswa.model.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

/**
 * Helper extension function untuk parsing JsonElement dengan aman.
 */
private fun JsonElement?.stringOrEmpty(): String =
    if (this is JsonPrimitive) this.content else ""

private fun JsonElement?.intOrZero(): Int =
    if (this is JsonPrimitive) this.intOrNull ?: 0 else 0

private fun JsonElement?.doubleOrZero(): Double =
    if (this is JsonPrimitive) this.doubleOrNull ?: 0.0 else 0.0

private fun JsonElement?.boolOrFalse(): Boolean =
    if (this is JsonPrimitive) this.booleanOrNull ?: false else false

suspend fun getSetoranListFromApi(token: String): List<Setoran> = withContext(Dispatchers.IO) {
    try {
        val response: HttpResponse = ApiClient.client.get(
            "https://api.tif.uin-suska.ac.id/setoran-dev/v1/mahasiswa/setoran-saya"
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        val fullResponse = response.body<JsonObject>()
        Log.d("API Response", "Full response: $fullResponse")

        val detailArray = fullResponse["data"]?.jsonObject
            ?.get("setoran")?.jsonObject
            ?.get("detail")?.jsonArray

        if (detailArray == null || detailArray.isEmpty()) {
            Log.e("API Response", "Detail array kosong atau tidak ditemukan")
            return@withContext emptyList()
        }

        val setoranList = detailArray.map { jsonElement ->
            val jsonObject = jsonElement.jsonObject

            val infoSetoran = jsonObject["info_setoran"]?.takeIf { it !is JsonNull }?.jsonObject?.let {
                InfoSetoran(
                    id = it["id"].stringOrEmpty(),
                    tgl_setoran = it["tgl_setoran"].stringOrEmpty(),
                    tgl_validasi = it["tgl_validasi"].stringOrEmpty(),
                    dosen_yang_mengesahkan = it["dosen_yang_mengesahkan"]?.jsonObject?.let { dosen ->
                        Dosen(
                            nip = dosen["nip"].stringOrEmpty(),
                            nama = dosen["nama"].stringOrEmpty(),
                            email = dosen["email"].stringOrEmpty()
                        )
                    }
                )
            }

            Setoran(
                id = jsonObject["id"].stringOrEmpty(),
                nama = jsonObject["nama"].stringOrEmpty(),
                nama_arab = jsonObject["nama_arab"].stringOrEmpty(),
                label = jsonObject["label"].stringOrEmpty(),
                sudah_setor = jsonObject["sudah_setor"].boolOrFalse(),
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

suspend fun getUserProfileFromApi(token: String): UserInfo? = withContext(Dispatchers.IO) {
    try {
        val userResponse: HttpResponse = ApiClient.client.get(
            "https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/userinfo"
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        if (userResponse.status.value != 200) {
            throw Exception("Gagal mengambil profil: ${userResponse.status.value}")
        }

        val userJson = userResponse.body<JsonObject>()
        Log.d("UserInfo", "Keycloak response: $userJson")

        val baseUser = UserInfo(
            sub = userJson["sub"].stringOrEmpty(),
            name = userJson["name"].stringOrEmpty(),
            preferred_username = userJson["preferred_username"].stringOrEmpty(),
            email = userJson["email"].stringOrEmpty()
        )

        val setoranResponse: HttpResponse = ApiClient.client.get(
            "https://api.tif.uin-suska.ac.id/setoran-dev/v1/mahasiswa/setoran-saya"
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        val setoranJson = setoranResponse.body<JsonObject>()
        Log.d("UserInfo", "SetoranSaya response: $setoranJson")

        val info = setoranJson["data"]?.jsonObject?.get("info")?.jsonObject

        val angkatan = info?.get("angkatan").stringOrEmpty()
        val semester = info?.get("semester")?.intOrZero() ?: 0
        val dosen = info?.get("dosen_pa")?.jsonObject

        val dosenPA = DosenPA(
            nama = dosen?.get("nama").stringOrEmpty(),
            nip = dosen?.get("nip").stringOrEmpty(),
            email = dosen?.get("email").stringOrEmpty()
        )

        return@withContext baseUser.copy(
            angkatan = angkatan,
            semester = semester,
            dosen_pa = dosenPA
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("API Response", "Gagal mengambil data profil lengkap: ${e.message}")
        return@withContext null
    }
}

suspend fun getRingkasanSetoranFromApi(token: String): List<RingkasanSetoran> = withContext(Dispatchers.IO) {
    try {
        Log.d("API Ringkasan", "Mulai ambil ringkasan setoran...")

        val response: HttpResponse = ApiClient.client.get(
            "https://api.tif.uin-suska.ac.id/setoran-dev/v1/mahasiswa/setoran-saya"
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        val responseText = response.bodyAsText()
        Log.d("API Ringkasan", "Raw response text: $responseText")

        val responseJson = Json.parseToJsonElement(responseText).jsonObject
        val ringkasanArray = responseJson["data"]
            ?.jsonObject
            ?.get("setoran")
            ?.jsonObject
            ?.get("ringkasan")
            ?.jsonArray

        if (ringkasanArray == null || ringkasanArray.isEmpty()) {
            Log.e("API Ringkasan", "Ringkasan tidak ditemukan dalam data JSON")
            return@withContext emptyList()
        }

        Log.d("API Ringkasan", "Ringkasan ditemukan: $ringkasanArray")

        val ringkasanList = ringkasanArray.map { item ->
            val obj = item.jsonObject
            RingkasanSetoran(
                label = obj["label"].stringOrEmpty(),
                total_wajib_setor = obj["total_wajib_setor"].intOrZero(),
                total_sudah_setor = obj["total_sudah_setor"].intOrZero(),
                total_belum_setor = obj["total_belum_setor"].intOrZero(),
                persentase_progres_setor = obj["persentase_progres_setor"].doubleOrZero()
            )
        }

        return@withContext ringkasanList

    } catch (e: Exception) {
        Log.e("API Ringkasan", "Gagal ambil ringkasan: ${e.message}")
        e.printStackTrace()
        return@withContext emptyList()
    }
}


