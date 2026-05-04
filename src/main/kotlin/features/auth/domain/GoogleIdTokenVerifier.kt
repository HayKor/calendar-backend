package com.haykor.features.auth.domain

import com.haykor.features.auth.presentation.GoogleUserDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class GoogleIdTokenVerifier(
    private val httpClient: HttpClient,
//    private val serverClientId: String,
) {
    suspend fun verify(idToken: String): GoogleUserDTO? = httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
        parameter("id_token", idToken) // FIX: HTTP 500 on invalid idToken
    }.body<GoogleUserDTO>()
}
