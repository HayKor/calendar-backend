package com.haykor.features.auth.presentation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleIdTokenRequest(
    @SerialName("id_token") val idToken: String,
)
