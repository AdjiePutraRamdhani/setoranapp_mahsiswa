package com.example.setoranhapalanmahasiswa.network

import com.example.setoranhapalanmahasiswa.model.Setoran
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.*

suspend fun getSetoranListFromApi(token: String): List<Setoran> {
    val response = ApiClient.client.get("https://api.tif.uin-suska.ac.id/setoran-dev/v1/mahasiswa/setoran-saya") {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
    val fullResponse = response.body<JsonObject>()
    val detailArray = fullResponse["data"]?.jsonObject?.get("setoran")?.jsonObject?.get("detail")?.jsonArray ?: JsonArray(emptyList())
    return detailArray.map { jsonElement ->
        val jsonObject = jsonElement.jsonObject
        Setoran(
            id = jsonObject["id"]?.jsonPrimitive?.content ?: "",
            surah = jsonObject["nama"]?.jsonPrimitive?.content ?: "",
            ayat = jsonObject["nama_arab"]?.jsonPrimitive?.content ?: "",
            label = jsonObject["label"]?.jsonPrimitive?.content ?: "",
            sudah_setor = jsonObject["sudah_setor"]?.jsonPrimitive?.boolean ?: false

        )
    }
}
