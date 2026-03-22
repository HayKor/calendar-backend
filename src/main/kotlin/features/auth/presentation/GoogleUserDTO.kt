package com.haykor.features.auth.presentation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserDTO(
    @SerialName("sub") val id: String,
    val email: String,
    @SerialName("email_verified") val verifiedEmail: Boolean = false,
    val name: String,
    @SerialName("given_name") val firstName: String,
    @SerialName("family_name") val lastName: String? = null,
    val picture: String? = null,
    val locale: String? = null
)