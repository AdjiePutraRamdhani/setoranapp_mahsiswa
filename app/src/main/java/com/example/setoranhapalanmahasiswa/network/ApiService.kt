package com.example.setoranhapalanmahasiswa.network

import com.example.setoranhapalanmahasiswa.datastore.DataStoreManager
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

// Fungsi untuk mengambil data setoran mahasiswa
suspend fun getSetoranListFromApi(): List<Setoran> {
    return try {
        // Melakukan request ke API setoran
        val response: HttpResponse = ApiClient.client.get("https://api.tif.uin-suska.ac.id/setoran-dev/v1/mahasiswa/setoran-saya")

        // Parsing respons menjadi JsonObject
        val fullResponse = response.body<JsonObject>()

        // Mengambil array detail setoran
        val detailArray = fullResponse["data"]?.jsonObject
            ?.get("setoran")?.jsonObject
            ?.get("detail")?.jsonArray ?: JsonArray(emptyList())

        // Mapping JSON menjadi objek Setoran
        detailArray.map { jsonElement ->
            val jsonObject = jsonElement.jsonObject
            Setoran(
                id = jsonObject["id"]?.jsonPrimitive?.content ?: "",
                surah = jsonObject["nama"]?.jsonPrimitive?.content ?: "",
                ayat = jsonObject["nama_arab"]?.jsonPrimitive?.content ?: "",
                label = jsonObject["label"]?.jsonPrimitive?.content ?: "",
                sudah_setor = jsonObject["sudah_setor"]?.jsonPrimitive?.boolean ?: false
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// Fungsi untuk mengambil data profil mahasiswa dari API Keycloak
suspend fun getUserProfileFromApi(token: String): UserInfo? {
    return try {
        val response: HttpResponse = ApiClient.client.get("https://id.tif.uin-suska.ac.id/realms/dev/protocol/openid-connect/userinfo") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        val fullResponse = response.body<JsonObject>()
        UserInfo(
            sub = fullResponse["sub"]?.jsonPrimitive?.content ?: "",
            name = fullResponse["name"]?.jsonPrimitive?.content ?: "",
            preferred_username = fullResponse["preferred_username"]?.jsonPrimitive?.content ?: "",
            email = fullResponse["email"]?.jsonPrimitive?.content ?: ""
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
