package com.haykor.features.auth.domain.service

import com.haykor.features.auth.presentation.model.GoogleUserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class GoogleIdTokenVerifier(
    private val httpClient: HttpClient,
//    private val serverClientId: String,
) {
    suspend fun verify(idToken: String): GoogleUserDTO? = httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
        parameter("id_token", idToken) // FIX: HTTP 500 on invalid idToken
    }.body<GoogleUserDTO>()
}