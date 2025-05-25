package com.jmoreira7.scrlcanvas.data

import android.util.Log
import com.jmoreira7.scrlcanvas.BuildConfig
import com.jmoreira7.scrlcanvas.data.model.OverlayCategory
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
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
        val response = retryWithBackoff {
            myHttpClient.get(BuildConfig.API_URL)
        }

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

    private suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelay: Long = 1000L,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay

        repeat(maxRetries - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay *= 2

                if (attempt < maxRetries - 1) {
                    Log.w(
                        TAG,
                        "Attempt ${attempt + 1} failed: ${e.message}. Retrying in ${currentDelay}ms"
                    )
                } else {
                    Log.e(TAG, "Max retries reached. Last error: ${e.message}")
                }
            }
        }
        return block()
    }


    companion object {
        private const val TAG = "AppostropheApiManager"
        private const val THIRTY_SECONDS = 30000L
    }
}