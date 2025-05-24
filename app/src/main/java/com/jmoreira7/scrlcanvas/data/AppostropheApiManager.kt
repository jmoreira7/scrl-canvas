package com.jmoreira7.scrlcanvas.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class AppostropheApiManager {
    val myHttpClient = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = TEN_SECONDS
        }
        install(ContentNegotiation) {
            json()
        }
    }

    companion object {
        private const val TEN_SECONDS = 10000L
    }
}