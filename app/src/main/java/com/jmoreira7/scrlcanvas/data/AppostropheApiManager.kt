package com.jmoreira7.scrlcanvas.data

import android.util.Log
import com.jmoreira7.scrlcanvas.BuildConfig
import com.jmoreira7.scrlcanvas.data.model.OverlayCategory
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class AppostropheApiManager : ApiManager {
    private val myHttpClient = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = THIRTY_SECONDS
        }
        install(ContentNegotiation) {
            json()
        }
    }

    override suspend fun fetchOverlays(): Flow<List<OverlayCategory>> = flow {
        val response: HttpResponse? = try {
            myHttpClient.get(BuildConfig.API_URL)
        } catch (e: Exception) {
            Log.i(TAG, "Error fetching overlays: ${e.message}")
            null
        }

        response?.let {
            when (response.status.value) {
                in 200..299 -> {
                    try {
                        emit(Json.decodeFromString<List<OverlayCategory>>(response.bodyAsText()))
                        Log.i(TAG, "Overlays fetched successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing response: ${e.message}")
                    }
                }

                else -> {
                    Log.e(
                        TAG,
                        "Error fetching overlays: ${response.status.value} - ${response.bodyAsText()}"
                    )
                }
            }
        }
    }


    companion object {
        private const val TAG = "AppostropheApiManager"
        private const val THIRTY_SECONDS = 30000L
    }
}